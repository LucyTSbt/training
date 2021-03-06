package ru.sbt.echo.server;


public class EchoServer {

    public static void main(String[] args){

        // если предполагается работа в рамках интерфейса без использования специфики реализации (такое решение наиболее предпочтительно),
        // то создание серверов лучше сделать примерно так: IServer server = new Server();
        // в противном случае при развитии кода другие разработчики могу наприкручивать методы в реализацию
        // и тем самым повысят связываемость модулей.
        Server server = new Server();
        StopServer stopServer = new StopServer(server);
        stopServer.serverStart();
        server.serverStart();
    }

}
