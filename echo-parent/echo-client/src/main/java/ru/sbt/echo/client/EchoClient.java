package ru.sbt.echo.client;

import ru.sbt.echo.Constant;

/**
 * Клиент для соединения с echo сервером
 */
public class EchoClient {

    public static void main(String[] args){

        Client client1 = new Client(Constant.DEFAULT_HOST, Constant.DEFAULT_PORT);
        client1.start();
        Client client2 = new Client(Constant.DEFAULT_HOST, Constant.DEFAULT_PORT);
        client2.start();
        // отправка правильного запроса серверу
        client1.sendRequest(Constant.TEST_REQUEST);
        // отправка не правильного запроса серверу
        client2.sendRequest(Constant.UNKNOWN_REQUEST);
        // проверка ответа
        client1.waitResponse(3000L);
        client1.validateResponse();
        client2.waitResponse(3000L);
        client2.validateResponse();
        // выход
        client1.sendRequest(Constant.EXIT);
        client1.close();
        client2.sendRequest(Constant.EXIT);
        client2.close();

    }

}
