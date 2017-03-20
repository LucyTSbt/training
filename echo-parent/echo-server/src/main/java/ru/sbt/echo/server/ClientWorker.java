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


    public ClientWorker(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
    }

    @Override
    public void run() {
        try {
            logger.debug("Echo Server: Hello Client {}!", clientNumber);
            initStreams();
            sendMessage("Hello!");
            String mes;
            while ((mes = readMessage()) != null) {
                if (Constant.EXIT.equals(mes)) {
                    logger.info("Echo Server: Client {} exit", clientNumber);
                    break;

                } else {
                    handleMessage(mes);
                }
            }

        } catch (IOException e) {
            logger.printError(e);
        } finally {
            close();
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.printError(e);
        }
    }


    /**
     * Инициализация потоков ввода/вывода
     *
     * @throws IOException
     */
    private void initStreams() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        out.flush();
        in = new Scanner(socket.getInputStream());
    }

    /**
     * Отправить ответ
     *
     * @param message сообщение
     */
    private void sendMessage(String message) {
        logger.debug("Echo Server to Client {}: {}", clientNumber, message);
        out.println(message);
        out.flush();
    }

    /**
     * Прочитать сообщение
     *
     * @return сообщение
     */
    private String readMessage() {
        return in.hasNextLine() ? in.nextLine().trim() : null;
    }

    // отдельно - бизнес логика.
    private void handleMessage(String mes) {
        if (Constant.TEST_REQUEST.compareToIgnoreCase(mes) == 0) {
            sendMessage(Constant.TEST_RESPONSE);
        } else {
            sendMessage(Constant.UNKNOWN_REQUEST);
        }
    }
}
