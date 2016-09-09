package ru.sbt.echo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbt.echo.Constant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Сервер
 */
public class Server {

    private final static Logger LOGGER  = LoggerFactory.getLogger(Server.class);

    private ServerSocket serverSocket;
    private ExecutorService executorService;

    public Server(){
        // создать сокет
        try{
            serverSocket = new ServerSocket(Constant.DEFAULT_PORT);
            executorService = Executors.newCachedThreadPool();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void start(){
        try{
            System.out.println("Echo Server start");
            LOGGER.info("Echo Server start");
            // ожидание обращения
            int clientNumber = 1;
            while (true) {
                Socket socket = serverSocket.accept();
                LOGGER.debug("Echo Server: Hello Client {}!", clientNumber);
                ClientSession clientSession = new ClientSession(socket, clientNumber);
                executorService.execute(clientSession);
                clientNumber++;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close();
        }
    }

    private void close(){
        try {
            serverSocket.close();
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            executorService.shutdownNow();
        }
    }
}
