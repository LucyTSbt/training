package ru.sbt.echo.client;

import ru.sbt.echo.Constant;
import ru.sbt.echo.EchoLogger;

import java.util.Scanner;

/**
 * Класс считывает сообщения сервера
 */
class ServerWorker implements Runnable {

    private final EchoLogger logger = EchoLogger.getEchoLogger(ServerWorker.class);

    private final Scanner in;
    private Boolean stopped;
    private String response;

    public ServerWorker(Scanner in){
        this.stopped = false;
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
        logger.info("Echo Client: Hello!");

        response = null;
        while (!stopped && in.hasNextLine()) {
            String line = in.nextLine();
            logger.debug("Response: {}", line);
            if (line.equals(Constant.TEST_RESPONSE) || line.equals(Constant.UNKNOWN_REQUEST)) {
                response = line;
            }
        }

    }

}
