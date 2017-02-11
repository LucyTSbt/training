package ru.sbt.echo.server;

import ru.sbt.echo.Constant;
import ru.sbt.echo.EchoLogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Обработка данных клиента
 */
public class ClientWorker implements Runnable {

    private final EchoLogger logger = EchoLogger.getEchoLogger(ClientWorker.class);

    private final Socket socket;
    private final int clientNumber;
    private PrintWriter out;
    private Scanner in;


    public ClientWorker(Socket socket, int clientNumber){
        this.socket = socket;
        this.clientNumber = clientNumber;
//        try{
//        // АИ: мне не нравится что, конструктор помимо инициализации полей содержит в себе и некоторую логику обработки соединения
//        // признаком этого служит необходимость ловить эксепшен в конструкторе.
//        // логику работы лучше вынести в метод run(); - тогда она будет обрабатываться в новом потоке, а не подвешивать или ломать текущий,
//        // если какие-то вызовы окажутся блокирующими или кинут RuntimeException.
//            out = new PrintWriter(socket.getOutputStream(), true);
//            out.flush();
//            in = new Scanner(socket.getInputStream());
//        } catch (IOException e) {
//            LOGGER.error(e.getMessage(), e);
//            close();
//        }
    }
    @Override
    public void run() {
        try {
            logger.debug("Echo Server: Hello Client {}!", clientNumber);
            // получить потоки ввода/вывода
            out = new PrintWriter(socket.getOutputStream(), true);
            out.flush();
            in = new Scanner(socket.getInputStream());
            sendMessage("Hello!"); // логика ответа вынесена в отдельный метод согласно SRP - это хорошо
            // Хорошим стилем будет также выненести и логику запроса. Тогда комментарий в следующей строке будет не нужен.
            getMessage();
        } catch (IOException e) {
            logger.printError(e);
        } finally {
            close();
        }
    }

    public void close(){
        try{
            socket.close();
        } catch (IOException e) {
            logger.printError(e);
        }
    }

    private void sendMessage(String message){
        logger.debug("Echo Server to Client {}: {}", clientNumber, message);
        out.println(message);
        out.flush();
    }

    private void getMessage(){
        Boolean isExit = false;
        while (!isExit && in.hasNextLine()) {
            String line = in.nextLine().trim();
            if (line.compareToIgnoreCase(Constant.TEST_REQUEST) == 0) {
                sendMessage(Constant.TEST_RESPONSE);
            } else {
                if (line.equals("exit")) {
                    logger.info("Echo Server: Client {} exit", clientNumber);
                    isExit = true;
                } else {
                    sendMessage(Constant.UNKNOWN_REQUEST);
                }
            }
        }
    }
}
