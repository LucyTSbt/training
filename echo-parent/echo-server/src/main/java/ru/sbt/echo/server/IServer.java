package ru.sbt.echo.server;

/**
 * Интерфейс сервера
 */
interface IServer {
    /**
     * Старт сервера
     */
    void serverStart();

    /**
     * Остановка сервера
     */
    void serverStop();

}
