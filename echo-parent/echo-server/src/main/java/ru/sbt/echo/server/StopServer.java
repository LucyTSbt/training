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

/**
 * Сервер для остановки echo-server
 */
public class StopServer extends Thread implements IServer {

    private final Logger LOGGER = LoggerFactory.getLogger(StopServer.class);
    private final Server server;
    private ServerSocket serverSocket;

    public StopServer(Server server){
        this.server = server;
        //setDaemon(true);
        try {
            serverSocket = new ServerSocket(Constant.STOP_PORT);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        LOGGER.info("Echo Server listens on port {}", Constant.STOP_PORT);

        try {
            Socket socket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.flush();
            String input;

            while ((input = in.readLine()) != null) {
                if (input.equalsIgnoreCase("stop")) {
                    out.println(input);
                    out.flush();
                    LOGGER.info("Echo Server received a stop command");
                    break;
                }
            }
            out.close(); // если здесь будет эксепшен, то in и socket остануться не закрытыми
            in.close();
            socket.close();
            serverStop();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
    }


    @Override
    public void serverStart(){
        this.start();
    }


    @Override
    public void serverStop() {
        try {
            serverSocket.close(); // если будет эксепшен, то сервер останется неостановленным.
            server.serverStop();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
    }
}
