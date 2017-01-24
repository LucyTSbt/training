package ru.sbt.echo.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbt.echo.server.ClientWorker;

import java.util.LinkedList;
import java.util.List;

/**
 * Класс для выполнения задач в потоках
 */
public class WorkerService {

    private final Logger Logger = LoggerFactory.getLogger(WorkerService.class);

    private final List<ClientWorker> workers;
    private final Object monitor;

    public WorkerService(){
        this.workers = new LinkedList<>();
        this.monitor = new Object();
    }

    /**
     * выполнить задачу
     */
    public void execute(ClientWorker worker){
        // в таком раскладе можно очень быстро потерять сервер - при одновременно открытии большого числа соединений,
        // получится большое количество одновременно запущенных потоков.
        // количество одновременных потоков нужно ограничивать.
        // А лучше создать пул потоков, а не создавать каждый раз новый.
        Thread thread = new Thread(worker);
        thread.setDaemon(true);
        addWorker(worker);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Logger.error(e.getMessage());
            Logger.debug(e.getMessage(), e);
        }
        closeWorker(worker);
    }

    /**
     * Завершить выполнение всех задач
     * освободить ресурсы
     * завершить потоки
     */
    public void shutdown(){
        workers.forEach(this::closeWorker);
        // есть ощущение, что при такой остановке можно получить пачку эксепшенов при закрытии worker'в
        // worker.close(); закрывает сокет, если в worker.run() с этим сокетом происходит какая-то работа, то будет эксепшен,
        // который нигде не ловится.
    }

    private void closeWorker(ClientWorker worker){
        worker.close();
        removeWorker(worker);
    }

    /**
     * добавить задачу
     */
    private void addWorker(ClientWorker worker){
        synchronized (monitor) {
            workers.add(worker);
        }
    }

    private void removeWorker(ClientWorker worker){
        synchronized (monitor) {
            workers.remove(worker);
        }
    }
}
