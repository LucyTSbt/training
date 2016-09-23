package ru.sbt.echo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbt.echo.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Сервер
 */
public class Server extends Thread {

    private final static Logger LOGGER  = LoggerFactory.getLogger(Server.class);

    private ServerSocket serverSocket;
    private ServerSocket serverStopSocket;
    private ExecutorService executorService;
    private Boolean stopped;

    public Server(){
        // создать сокет
        try{
            serverSocket = new ServerSocket(Constant.DEFAULT_PORT);
            serverStopSocket = new ServerSocket(Constant.STOP_PORT);
            executorService = Executors.newCachedThreadPool();
            stopped = false;
            //setDaemon(true);
            start();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
    }

    @Override
    public void run(){
        try{
            System.out.println("Echo Server start");
            LOGGER.info("Echo Server start on port {}", Constant.DEFAULT_PORT);
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

    public void serverStart(){
        LOGGER.info("Echo Server listens on port {}", Constant.STOP_PORT);

        try (Socket socket = serverStopSocket.accept()) {
            BufferedReader in  = new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            String input;

            while ((input = in.readLine()) != null) {
                if (input.equalsIgnoreCase("stop")){
                    out.println(input);
                    LOGGER.info("Echo Server received a stop command");
                    break;
                }
            }
            out.close();
            in.close();
            socket.close();
            serverStop();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private void serverStop(){
        try {
            LOGGER.info("Echo Server is stopping");
            stopped = true;
            serverStopSocket.close();
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
