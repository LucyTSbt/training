package ru.sbt.echo.server;

import ru.sbt.echo.Constant;
import ru.sbt.echo.EchoLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Сервер для остановки echo-server
 */
public class StopServer extends Thread implements IServer {

    private final EchoLogger logger = EchoLogger.getEchoLogger(StopServer.class);
    private final IServer server;
    private ServerSocket serverSocket;
    private Boolean stopped;

    public StopServer(IServer server){
        this.server = server;
    }

    @Override
    public void run() {
        init();
        if (!isStopped()) {
            try (Socket socket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.flush();
                String input;

                while (!isStopped() && (input = in.readLine()) != null) {
                    if (input.equalsIgnoreCase("stop")) {
                        out.println(input);
                        out.flush();
                        logger.info("Echo Server received a stop command");
                        break;
                    }
                }
            } catch (IOException e) {
                logger.printError(e);
            } finally {
                shutdown();
            }
        } else {
            terminate();
        }
    }


    @Override
    public void serverStart(){
        this.start();
    }


    @Override
    public void serverStop() {
        stopped = Boolean.TRUE;
    }

    @Override
    public Boolean isStopped() {
        return this.stopped;
    }

    private void init(){
        try {
            serverSocket = new ServerSocket(Constant.STOP_PORT);
            logger.info("Echo Server listens on port {}", Constant.STOP_PORT);
            stopped = Boolean.FALSE;
        } catch (IOException e) {
            logger.printError(e);
            stopped = Boolean.TRUE;
        }
    }

    private void shutdown(){
        terminate();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                logger.info("Echo Server is not listening on a port {}", Constant.STOP_PORT);
            } catch (IOException e) {
                logger.printError(e);
            }
        }
    }

    private void terminate(){
        if (server != null && !server.isStopped()) {
            server.serverStop();
            try (Socket socket = new Socket(Constant.DEFAULT_HOST, Constant.DEFAULT_PORT)) {
                logger.info("Echo Server terminating");
            } catch (IOException e) {
                logger.printError(e);
            }
        }
    }
}
