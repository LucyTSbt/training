package ru.sbt.echo.server;


public class EchoServer {

    public static void main(String[] args){

        Server server = new Server(); // запуск нового потока. С какой целью?
        StopServer stopServer = new StopServer(server);
        stopServer.serverStart();
        server.serverStart();
    }

}
