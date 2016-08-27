package ru.sbt.echo.client;

import ru.sbt.echo.Constant;

import java.util.Scanner;

/**
 * Класс считывает сообщения сервера
 */
public class ServerSession implements Runnable {

    private final Scanner in;
    private Boolean stopped;
    private String response;

    public ServerSession(Scanner in){
        this.stopped = true;
        this.in = in;
    }

    public Boolean isStopped() {
        return stopped;
    }

    public void setStopped() {
        this.stopped = true;
    }

    public String getResponse() {
        return response;
    }

    @Override
    public void run() {
        System.out.println("Echo Client: Hello!");

        stopped = false;
        response = null;
        while (!stopped && in.hasNextLine()) {
            String line = in.nextLine();
            System.out.println("Response: " + line);
            if (line.equals(Constant.TEST_RESPONSE) || line.equals(Constant.UNKNOWN_REQUEST)) {
                response = line;
            }
        }

    }

}
