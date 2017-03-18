package ru.sbt.echo.server;

import ru.sbt.echo.Constant;
import ru.sbt.echo.EchoLogger;
import ru.sbt.echo.worker.WorkerService;

import java.io.IOException;
import java.net.ServerSocket;


/**
 * Сервер
 *
 */
public class Server extends Thread implements IServer {

    private final EchoLogger logger = EchoLogger.getEchoLogger(Server.class);

    private ServerSocket serverSocket;
    private WorkerService workerService;
    private Boolean stopped = Boolean.TRUE;

    public Server() {
    }

    @Override
    public void run() {
        try {
            init();
            // ожидание обращения
            int clientNumber = 1;
            while (!stopped) {
                ClientWorker clientWorker = new ClientWorker(serverSocket.accept(), clientNumber);
                workerService.execute(clientWorker);
                clientNumber++;
            }
        } catch (IOException e) {
            logger.printError(e);
        } finally {
            shutdown();
        }
    }
    @Override
    public void serverStart(){
        this.start();
    }

    @Override
    public void serverStop() {
        this.stopped = Boolean.TRUE;
    }

    @Override
    public Boolean isStopped() {
        return this.stopped;
    }

    private void init() throws IOException {
        serverSocket = new ServerSocket(Constant.DEFAULT_PORT);
        workerService = new WorkerService();
        stopped = Boolean.FALSE;

        logger.print("Echo Server start");
        logger.info("Echo Server start on port {}", Constant.DEFAULT_PORT);
    }

    private void shutdown(){
        try {
            logger.info("Echo Server is stopping");
            workerService.shutdown();
            serverSocket.close();
        } catch (IOException e) {
            logger.printError(e);
        }
        logger.info("Echo Server stopped");
    }

}
