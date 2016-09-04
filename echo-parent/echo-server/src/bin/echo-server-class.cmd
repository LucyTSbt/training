@echo off
java -Dlogback.configurationFile=%cd:~0,-4%/conf/logback.xml -DUSER_HOME="%cd:~0,-4%" -classpath ../lib/echo-common.jar-1.0.0;echo-server-1.0.0.jar;../lib/slf4j-api-1.7.21.jar;../lib/logback-core-1.1.7.jar;../lib/logback-classic-1.1.7.jar ru.sbt.echo.server.EchoServer
pause