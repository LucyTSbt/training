package ru.sbt.echo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbt.echo.Constant;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Сервер
 * Нарушение Single Responsibility Principle: https://ru.wikipedia.org/wiki/%D0%9F%D1%80%D0%B8%D0%BD%D1%86%D0%B8%D0%BF_%D0%B5%D0%B4%D0%B8%D0%BD%D1%81%D1%82%D0%B2%D0%B5%D0%BD%D0%BD%D0%BE%D0%B9_%D0%BE%D0%B1%D1%8F%D0%B7%D0%B0%D0%BD%D0%BD%D0%BE%D1%81%D1%82%D0%B8
 * Этот класс сейчас спроектирован для использования в двух потоках и в каждом из них он выполняет свою функцию.
 * При этом один поток он запускает сам, а другой долже вызывать его извне: очень запутанная логика.
 * Нужно устранить нарушение SRP
 */
public class Server extends Thread implements IServer {

    private final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Boolean stopped;

    public Server() {
        // создать сокет
        try {
            serverSocket = new ServerSocket(Constant.DEFAULT_PORT);
            executorService = Executors.newCachedThreadPool();
            stopped = false;
            //setDaemon(true);
            //start(); // Почему так делать не нужно и другие полезные советы:  https://www.ibm.com/developerworks/ru/library/j-jtp0618/
            //start();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        System.out.println("Echo Server start");
        LOGGER.info("Echo Server start on port {}", Constant.DEFAULT_PORT);

        try {
            // ожидание обращения
            int clientNumber = 1;
            while (!stopped) {
                ClientWorker clientWorker = new ClientWorker(serverSocket.accept(), clientNumber);
                executorService.execute(clientWorker);
                clientNumber++;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        } finally {
            serverStop();
        }
    }
    @Override
    public void serverStart(){
        this.start();
    }

    @Override
    public void serverStop() {
        try {
            LOGGER.info("Echo Server is stopping");
            stopped = true;
            serverSocket.close();
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        } finally {
            executorService.shutdownNow();
        }
        LOGGER.info("Echo Server stoped");
    }

}