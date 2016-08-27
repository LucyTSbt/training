package ru.sbt.echo.server;

import ru.sbt.echo.Constant;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Обработка данных клиента
 */
public class ClientSession implements Runnable {

    private final Socket socket;
    private final int clientNumber;
    private PrintWriter out;
    private Scanner in;


    public ClientSession(Socket socket, int clientNumber){
        this.socket = socket;
        this.clientNumber = clientNumber;
        // получить потоки ввода/вывода
        try{
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }
    @Override
    public void run() {
        try {
            out.println("Echo Server: Hello!");
            // считать данные из строки
            Boolean isExit = false;
            while (!isExit && in.hasNextLine()) {
                String line = in.nextLine().trim();
                if (line.compareToIgnoreCase(Constant.TEST_REQUEST) == 0) {
                    out.println(Constant.TEST_RESPONSE);
                    System.out.println(String.format("Echo Server to Client %d: %s", clientNumber, Constant.TEST_RESPONSE));
                } else {
                    if (line.equals("exit")) {
                        System.out.println(String.format("Echo Server: Client %d exit", clientNumber));
                        isExit = true;
                    } else {
                        out.println(Constant.UNKNOWN_REQUEST);
                        System.out.println(String.format("Echo Server to Client %d: %s", clientNumber, Constant.UNKNOWN_REQUEST));
                    }
                }
            }
        } finally {
            close();
        }
    }

    public void close(){
        try{
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
