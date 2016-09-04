package ru.sbt.echo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbt.echo.Constant;

import java.util.Scanner;

/**
 * Класс считывает сообщения сервера
 */
public class ServerSession implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerSession.class);

    private final Scanner in;
    private Boolean stopped;
    private String response;

    public ServerSession(Scanner in){
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
        LOGGER.info("Echo Client: Hello!");

        response = null;
        while (!stopped && in.hasNextLine()) {
            String line = in.nextLine();
            LOGGER.debug("Response: {}", line);
            if (line.equals(Constant.TEST_RESPONSE) || line.equals(Constant.UNKNOWN_REQUEST)) {
                response = line;
            }
        }

    }

}
