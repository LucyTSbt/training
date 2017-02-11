package ru.sbt.echo.client;

import ru.sbt.echo.Constant;

/**
 * Клиент для соединения с echo сервером
 */
public class EchoClient {

    public static void main(String[] args){

        Client client1 = new Client(Constant.DEFAULT_HOST, Constant.DEFAULT_PORT);
        Client client2 = new Client(Constant.DEFAULT_HOST, Constant.DEFAULT_PORT);
        // отправка правильного запроса серверу
        client1.sendRequest(Constant.TEST_REQUEST, 3000L);
        // отправка не правильного запроса серверу
        client2.sendRequest(Constant.UNKNOWN_REQUEST, 3000L);
        // остановка сервера
        Client client3 = new Client(Constant.DEFAULT_HOST, Constant.STOP_PORT);
        client3.sendRequest("stop", 3000L);
    }

}
