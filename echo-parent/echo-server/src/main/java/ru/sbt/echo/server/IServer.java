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

    /**
     * Проверить, что сервер остановлен
     *
     * @return true, если сервер остановлен, иначе - false
     */
    Boolean isStopped();

}
