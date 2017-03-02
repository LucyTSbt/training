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
            // получить потоки ввода/вывода
            //следующие три строчки можно вынести в отдельный метод инициализации.
            // В него будет позже помещать любой код, относящийся к подготовке потока к работе.
            out = new PrintWriter(socket.getOutputStream(), true);
            out.flush();
            in = new Scanner(socket.getInputStream());

            sendMessage("Hello!"); // логика ответа вынесена в отдельный метод согласно SRP - это хорошо
            // Хорошим стилем будет также выненести и логику запроса. Тогда комментарий в следующей строке будет не нужен.
            getMessage(); // в jave принято, что метод, называющийся getXXX() возвращает значение свойства ХХХ.
                          // лучше его назвать как-нибудь по другому.. я ниже переименовал в readMessage
                          // readMessage - возвращает значение, но это значение не свойства. Другими словами,
                          // при прочих постоянных условиях в общем случае возвращаемое значение будет каждый раз разное,
                          // Поэтому не getMessage()

            //немного не то имел ввиду. я подразумевал что-то типо следующего:
            /* логика чтения сообщения
            String mes = readMessage();

            while (true) {
                if (Constant.EXIT.equals(mes)) {
                    logger.info("Echo Server: Client {} exit", clientNumber);
                    break;

                } else {
                    // здесь бизнес логика. у нас она простая,но когда будет сложная,
                    //  принципиально этот код не изменится
                    handleMessage(mes);
                }
            }
            */

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

    private void sendMessage(String message) {
        logger.debug("Echo Server to Client {}: {}", clientNumber, message);
        out.println(message);
        out.flush();
    }

    private void getMessage() {
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

    private String readMessage() {
        return in.nextLine().trim();
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
