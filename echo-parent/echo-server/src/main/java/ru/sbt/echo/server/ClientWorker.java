package ru.sbt.echo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbt.echo.Constant;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Обработка данных клиента
 */
public class ClientWorker implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientWorker.class);

    private final Socket socket;
    private final int clientNumber;
    private PrintWriter out;
    private Scanner in;


    public ClientWorker(Socket socket, int clientNumber){
        this.socket = socket;
        this.clientNumber = clientNumber;
        // получить потоки ввода/вывода
        try{
        // АИ: мне не нравится что, конструктор помимо инициализации полей содержит в себе и некоторую логику обработки соединения
        // признаком этого служит необходимость ловить эксепшен в конструкторе.
        // логику работы лучше вынести в метод run(); - тогда она будет обрабатываться в новом потоке, а не подвешивать или ломать текущий,
        // если какие-то вызовы окажутся блокирующими или кинут RuntimeException.
            out = new PrintWriter(socket.getOutputStream(), true);
            out.flush();
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            close();
        }
    }
    @Override
    public void run() {
        try {
            LOGGER.debug("Echo Server: Hello Client {}!", clientNumber);
            sendMessage("Hello!"); // логика ответа вынесена в отдельный метод согласно SRP - это хорошо
            // Хорошим стилем будет также выненести и логику запроса. Тогда комментарий в следующей строке будет не нужен.
            // считать данные из строки
            Boolean isExit = false;
            while (!isExit && in.hasNextLine()) {
                String line = in.nextLine().trim();
                if (line.compareToIgnoreCase(Constant.TEST_REQUEST) == 0) {
                    sendMessage(Constant.TEST_RESPONSE);
                } else {
                    if (line.equals("exit")) {
                        LOGGER.info("Echo Server: Client {} exit", clientNumber);
                        isExit = true;
                    } else {
                        sendMessage(Constant.UNKNOWN_REQUEST);
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
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void sendMessage(String message){
        LOGGER.debug("Echo Server to Client {}: {}", clientNumber, message);
        out.println(message);
        out.flush();
    }
}
