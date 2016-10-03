package ru.sbt.echo.server;

/**
 * Интерфейс сервера
 */
public interface IServer {
    /**
     * Старт сервера
     */
    void serverStart();

    /**
     * Остановка сервера
     */
    void serverStop();

}
