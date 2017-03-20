package ru.sbt.echo.server;


public class EchoServer {

    public static void main(String[] args){

        IServer server = new Server();
        server.serverStart();

        IServer stopServer = new StopServer(server);
        stopServer.serverStart();
    }
}
