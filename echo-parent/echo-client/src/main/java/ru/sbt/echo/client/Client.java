package ru.sbt.echo.client;

import ru.sbt.echo.Constant;
import ru.sbt.echo.EchoLogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 * Клиент
 */
class Client {

    private final EchoLogger logger = EchoLogger.getEchoLogger(Client.class);

    private Scanner in;
    private PrintWriter out;
    private Socket socket;
    private ServerWorker serverWorker;
    private final String host;
    private final int port;

    public Client(String host, int port){
        this.host = host;
        this.port = port;
    }

//    public void start(){
//        new Thread(serverWorker).start();
//        // это просто клиентский интерфейс, он не ожидает запросов, для него выполнения его логики
//        // не нужно запускать отдельный поток. В отдельном потоке можно выполнять сеанс связи, но это должно
//        // быть скрыто от пользователей этого интерфейса
//
//
//    }

    public void sendRequest(String request, Long time){
        try{
            // сокет для соединения с сервером
            socket = new Socket(host, port);
            // потоки ввода вывода
            out = new PrintWriter(socket.getOutputStream(), true);
            out.flush();
            in = new Scanner(socket.getInputStream());
            serverWorker = new ServerWorker(in);
            new Thread(serverWorker).start();
            sendMessage(request);
            waitResponse(time);
            validateResponse();
        } catch (IOException e) {
            logger.printError(e);
        } finally {
            close();
        }
    }

    private void waitResponse(Long time){
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
            logger.printError(e);
        }

    }

    private void validateResponse(){
        if (serverWorker.getResponse() != null) {
            switch (serverWorker.getResponse()) {
                case Constant.TEST_RESPONSE:
                    logger.debug("Echo Client: access is allowed!");
                    break;
                case Constant.UNKNOWN_REQUEST:
                    logger.debug("Echo Client: access denied");
                    break;
            }
        } else{
            logger.debug("Echo Client: no response");
        }
    }

    private void close(){
        try{
            sendMessage(Constant.EXIT);
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
            logger.info("Echo Client: connection closed");
        } catch (IOException e) {
            logger.printError(e);
        }
    }

    private void sendMessage(String request){
        // отправка запроса
        out.println(request);
        out.flush();
        logger.debug("Echo Client: {}", request);
    }

}
