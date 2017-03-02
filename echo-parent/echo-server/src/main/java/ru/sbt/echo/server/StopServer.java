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

    public StopServer(IServer server){
        this.server = server;
        try {
            serverSocket = new ServerSocket(Constant.STOP_PORT);
        } catch (IOException e) {
            logger.printError(e);
        }
    }

    @Override
    public void run() {
        logger.info("Echo Server listens on port {}", Constant.STOP_PORT);

        try (Socket socket = serverSocket.accept();
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){
            out.flush();
            String input;

            while ((input = in.readLine()) != null) {
                if (input.equalsIgnoreCase("stop")) {
                    out.println(input);
                    out.flush();
                    logger.info("Echo Server received a stop command");
                    break;
                }
            }
//            out.close(); // если здесь будет эксепшен, то in и socket остануться не закрытыми
//            in.close();
//            socket.close();
        } catch (IOException e) {
            logger.printError(e);
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
        // предполагается, что этот метод будет вызываться из другого потока,
        // при этом, он останавливает другой сервер и высвобождает ресурсы своего.
        // Делает как минимум две вещи и обе - в другом потоке - нарушение SRP
        // Нехорошо брать ресурсы в одном потоке, а высвобождать в другом.
        // Одно из этих действий явно лишнее. Это можно понять, если попробовать написать JavaDoc к интерфейсу IServer
        // Другое реализовано ненадлежащим образом.
        server.serverStop();
        try {
            serverSocket.close(); // если будет эксепшен, то сервер останется неостановленным.
        } catch (IOException e) {
            logger.printError(e);
        }
    }
}
