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
public class Client {

    private final static Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private Scanner in;
    private PrintWriter out;
    private Socket socket;
    private ServerSession serverSession;

    public Client(String host, int port){

        try{
            // сокет для соединения с сервером
            socket = new Socket(host, port);
            // потоки ввода вывода
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            serverSession = new ServerSession(in);
            new Thread(serverSession).start();
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            close();
        }
    }

    public void sendRequest(String request){
        // отправка запроса
        out.println(request);
        LOGGER.debug("Echo Client: {}", request);
    }

    public Boolean waitResponse(Long time){
        Long endTime = new Date().getTime() + time;

        try {
            while (new Date().getTime() < endTime &&
                    !serverSession.isStopped() &&
                    serverSession.getResponse() == null)
            {
                // ожидать ответа
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    public void validateResponse(){
        if (serverSession.getResponse() != null) {
            switch (serverSession.getResponse()) {
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
            serverSession.setStopped();
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
}
