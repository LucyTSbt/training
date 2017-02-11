package ru.sbt.echo.server;

import ru.sbt.echo.Constant;
import ru.sbt.echo.EchoLogger;
import ru.sbt.echo.worker.WorkerService;

import java.io.IOException;
import java.net.ServerSocket;


/**
 * Сервер
 * Нарушение Single Responsibility Principle: https://ru.wikipedia.org/wiki/%D0%9F%D1%80%D0%B8%D0%BD%D1%86%D0%B8%D0%BF_%D0%B5%D0%B4%D0%B8%D0%BD%D1%81%D1%82%D0%B2%D0%B5%D0%BD%D0%BD%D0%BE%D0%B9_%D0%BE%D0%B1%D1%8F%D0%B7%D0%B0%D0%BD%D0%BD%D0%BE%D1%81%D1%82%D0%B8
 * Этот класс сейчас спроектирован для использования в двух потоках и в каждом из них он выполняет свою функцию.
 * При этом один поток он запускает сам, а другой долже вызывать его извне: очень запутанная логика.
 * Нужно устранить нарушение SRP
 */
public class Server extends Thread implements IServer {

    private final EchoLogger logger = EchoLogger.getEchoLogger(Server.class);

    private ServerSocket serverSocket;
    private WorkerService workerService;
//    private ExecutorService executorService;
    private Boolean stopped;

    public Server() {
        // создать сокет
//        try {
//            serverSocket = new ServerSocket(Constant.DEFAULT_PORT);
//            workerService = new WorkerService();
//            stopped = false;
//
//            logger.print("Echo Server start");  // для сообщений пользователю лучше создать отдельный метод
//                                                      // что-то типа printMessage(String)
//                                                      // сообщений может быть много, а в процессе разработки стандартный вывод может быть
//                                                      // на что-нибудь заменен.
//            logger.info("Echo Server start on port {}", Constant.DEFAULT_PORT);
//            // для вывода в лог тоже лучше писать промежутчный метод.
//            // вообще, их лучше писать для всех third-party библиотек
//            // во-первых, их интерфейс достаточно универсальный, а для проекта требуется достаточно узкое использование
//            // и создание проектного интерфейса дисциплинирует остальных разработчиков использовать библиотеку единым образом на проекте.
//            // во-вторых библиотеке иногда содержат ошибки, которые как раз удобно обходить в промежуточном слое.
//        } catch (IOException e) {
//            logger.printError(e);
//        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Constant.DEFAULT_PORT);
            workerService = new WorkerService();
            stopped = false;
            StopServer stopServer = new StopServer(this);
            stopServer.serverStart();

            logger.print("Echo Server start");
            logger.info("Echo Server start on port {}", Constant.DEFAULT_PORT);
            // ожидание обращения
            int clientNumber = 1;
            while (!stopped) {
                ClientWorker clientWorker = new ClientWorker(serverSocket.accept(), clientNumber);
//                executorService.execute(clientWorker);
                workerService.execute(clientWorker);
                clientNumber++;
            }
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
        try {
            logger.info("Echo Server is stopping");
            workerService.shutdown();
            stopped = true;
            serverSocket.close();
//            executorService.shutdown();
//            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (IOException e) {
            logger.printError(e);
        }
//        finally {
//            executorService.shutdownNow();
//
//        }
        logger.info("Echo Server stopped");
    }

}
