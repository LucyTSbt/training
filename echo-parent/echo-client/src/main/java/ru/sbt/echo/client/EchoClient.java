package ru.sbt.echo.client;

import ru.sbt.echo.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Клиент для соединения с echo сервером
 */
public class EchoClient {

    public static void main(String[] args){

        // отправка правильного запроса серверу
        sendRequest(Constant.DEFAULT_HOST, Constant.DEFAULT_PORT, Constant.TEST_REQUEST);
        // отправка не правильного запроса серверу
        sendRequest(Constant.DEFAULT_HOST, Constant.DEFAULT_PORT, Constant.UNKNOWN_REQUEST);

    }

    private static void sendRequest(String host, int port, String request) {

        try{
            // сокет для соединения с сервером
            try (Socket socket = new Socket(host, port)) {
                System.out.println("Echo Client: Hello!");
                OutputStream outputStream = socket.getOutputStream();
                // отправка запроса
                outputStream.write(request.getBytes());
                System.out.println("Echo Client: " + request);
            } finally {
                System.out.println("Echo Client: connection closed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
