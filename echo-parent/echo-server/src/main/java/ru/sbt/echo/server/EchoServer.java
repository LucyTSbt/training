package ru.sbt.echo.server;

import ru.sbt.echo.Constant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    public static void main(String[] args){

        // создать сокет
        try(ServerSocket serverSocket = new ServerSocket(Constant.DEFAULT_PORT)) {
            System.out.println("Echo Server start");
            // ожидание обращения
            int clientNumber = 1;
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(String.format("Echo Server: Hello Client %d!", clientNumber));
                ClientSession clientSession = new ClientSession(socket, clientNumber);
                new Thread(clientSession).start();
                clientNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
