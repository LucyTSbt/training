package ru.sbt.echo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbt.echo.Constant;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;

/**
 * Клиент
 */
class Client {

    private final static Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private Scanner in;
    private PrintWriter out;
    private Socket socket;
    private ServerWorker serverWorker;

    public Client(String host, int port){

        try{
            // сокет для соединения с сервером
            socket = new Socket(host, port);
            // потоки ввода вывода
            out = new PrintWriter(socket.getOutputStream(), true);
            out.flush();
            in = new Scanner(socket.getInputStream());
            serverWorker = new ServerWorker(in);
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            close();
        }
    }

    public void start(){
        new Thread(serverWorker).start();
        // это просто клиентский интерфейс, он не ожидает запросов, для него выполнения его логики
        // не нужно запускать отдельный поток. В отдельном потоке можно выполнять сеанс связи, но это должно
        // быть скрыто от пользователей этого интерфейса
    }

    public void sendRequest(String request){
        // отправка запроса
        out.println(request);
        out.flush();
        LOGGER.debug("Echo Client: {}", request);
    }

    public void waitResponse(Long time){
        Long endTime = new Date().getTime() + time;

        try {
            while (new Date().getTime() < endTime &&
                    !serverWorker.isStopped() &&
                    serverWorker.getResponse() == null)
            {
                // ожидать ответа
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public void validateResponse(){
        if (serverWorker.getResponse() != null) {
            switch (serverWorker.getResponse()) {
                case Constant.TEST_RESPONSE:
                    LOGGER.debug("Echo Client: access is allowed!");
                    break;
                case Constant.UNKNOWN_REQUEST:
                    LOGGER.debug("Echo Client: access denied");
                    break;
            }
        } else{
            LOGGER.debug("Echo Client: no response");
        }
    }

    public void close(){
        try{
            sendRequest(Constant.EXIT);
            serverWorker.setStopped();
            if(out != null) {
                out.close();
            }
            if (in != null){
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
            LOGGER.info("Echo Client: connection closed");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

//    public void stopServer(){
//        try(Socket stopSocket = new Socket(Constant.DEFAULT_HOST, Constant.STOP_PORT)) {
//            // потоки ввода вывода
//            BufferedReader in  = new BufferedReader(new
//                    InputStreamReader(stopSocket.getInputStream()));
//            PrintWriter out = new PrintWriter(stopSocket.getOutputStream(),true);
//            out.println("stop");
//            out.close();
//            in.close();
//            LOGGER.debug("Echo Client: stop");
//        } catch (UnknownHostException e) {
//            LOGGER.error(e.getMessage());
//            LOGGER.debug(e.getMessage(), e);
//        } catch (IOException e) {
//            LOGGER.error(e.getMessage());
//            LOGGER.debug(e.getMessage(), e);
//        }
//    }

}
