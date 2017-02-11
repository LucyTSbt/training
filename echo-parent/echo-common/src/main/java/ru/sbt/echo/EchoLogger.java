package ru.sbt.echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoLogger {
    private final Logger logger;

    private EchoLogger(Logger logger) {
        this.logger = logger;
    }

    public static EchoLogger getEchoLogger(Class<?> clazz){
        return new EchoLogger(LoggerFactory.getLogger(clazz));
    }

    public void info(String message){
        logger.info(message);
    }

    public void info(String message, Object obj){
        logger.info(message, obj);
    }

    public void printError(Throwable throwable){
        logger.error(throwable.getMessage());
        logger.debug(throwable.getMessage(), throwable);
    }

    public void debug(String message){
        logger.debug(message);
    }

    public void debug(String message, Object obj){
        logger.debug(message, obj);
    }

    public void debug(String message, Object obj1, Object obj2){
        logger.debug(message, obj1, obj2);
    }

    public void print(String message){
        System.out.println(message);
    }
}
