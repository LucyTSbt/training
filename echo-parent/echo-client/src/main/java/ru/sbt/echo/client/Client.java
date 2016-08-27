package ru.sbt.echo.client;

import ru.sbt.echo.Constant;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Клиент
 */
public class Client {

    private Scanner in;
    private PrintWriter out;
    private Socket socket;
    private ServerSession serverSession;

    public Client(String host, int port){

        try{
            // сокет для соединения с сервером
            Socket socket = new Socket(host, port);
            // потоки ввода вывода
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            serverSession = new ServerSession(in);
            new Thread(serverSession).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public void sendRequest(String request){
        // отправка запроса
        out.println(request);
        System.out.println("Echo Client: " + request);
    }

    public void validateResponse(){
        try {
            while (!serverSession.isStopped() && serverSession.getResponse() == null) {
                // ожидать ответа
                Thread.sleep(1000);
            }
            switch (serverSession.getResponse()) {
                case Constant.TEST_RESPONSE:
                    System.out.println("Echo Client: access is allowed!");
                    break;
                case Constant.UNKNOWN_REQUEST:
                    System.out.println("Echo Client: access denied");
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try{
            serverSession.setStopped();
            if(out != null) {
                out.close();
            }
            if (in != null){
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
            System.out.println("Echo Client: connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
