package ru.sbt.echo.server;

import ru.sbt.echo.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class EchoServer {

    public static void main(String[] args){

        try {
            // создать сокет
            ServerSocket serverSocket = new ServerSocket(Constant.DEFAULT_PORT);
            // ожидание обращения
            try (Socket socket = serverSocket.accept()) {
                // получить потоки ввода/вывода
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter out = new PrintWriter(outputStream, true);
                out.println("Echo Server: Hello! Enter exit to quit.");
                // считать данные из строки
                Scanner in = new Scanner(inputStream);
                Boolean isExit = false;
                while (!isExit && in.hasNextLine()) {
                    String line = in.nextLine().trim();
                    if (line.compareToIgnoreCase(Constant.TEST_REQUEST) == 0) {
                        out.println("Echo Server: " + Constant.TEST_RESPONSE);
                    } else{
                        if(line.equals("exit")){
                            isExit = true;
                        } else {
                            out.println("Echo Server: " + Constant.UNKNOWN_REQUEST);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
