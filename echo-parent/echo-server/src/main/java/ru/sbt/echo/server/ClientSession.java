package ru.sbt.echo.server;

import ru.sbt.echo.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Обработка данных клиента
 */
public class ClientSession implements Runnable {

    private Socket socket;
    private int clientNumber;

    public ClientSession(Socket socket, int clientNumber){
        this.socket = socket;
        this.clientNumber = clientNumber;
    }
    @Override
    public void run() {

        try{
            try {
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
                        out.println(String.format("Echo Server: %s", Constant.TEST_RESPONSE));
                        System.out.println(String.format("Echo Server to Client %d: %s", clientNumber, Constant.TEST_RESPONSE));
                    } else {
                        if (line.equals("exit")) {
                            System.out.println(String.format("Echo Server: Client %d exit", clientNumber));
                            isExit = true;
                        } else {
                            out.println("Echo Server: " + Constant.UNKNOWN_REQUEST);
                            System.out.println(String.format("Echo Server to Client %d: %s", clientNumber, Constant.UNKNOWN_REQUEST));
                        }
                    }
                }
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
