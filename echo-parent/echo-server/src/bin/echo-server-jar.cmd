@echo off
java -Dlogback.configurationFile=%cd:~0,-4%/conf/logback.xml -DUSER_HOME="%cd:~0,-4%" -jar echo-server-1.0.0.jar
pause