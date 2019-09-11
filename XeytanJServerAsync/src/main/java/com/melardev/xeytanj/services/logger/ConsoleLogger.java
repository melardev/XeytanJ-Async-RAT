package com.melardev.xeytanj.services.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class ConsoleLogger implements ILogger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public ConsoleLogger() {
    }

    Logger logger = LogManager.getLogger();

    @PostConstruct
    void configure() {
        System.out.println(logger);
    }

    @Override
    public void debug(String text) {
        logger.debug(text);
    }

    private void print(String color, String text) {
        System.out.println(color + text + ANSI_RESET);

    }

    @Override
    public void wran(String text) {
        logger.warn(text);
    }

    @Override
    public void error(String text) {
        logger.error(text);
    }

    @Override
    public void errorFormat(String s, String... args) {
        logger.error(String.format(s, args));
    }

    private void printf(String color, String text, String... args) {
        System.out.println(color + String.format(text, (Object[]) args) + ANSI_RESET);
    }

    @Override
    public void trace(String text) {
        logger.trace(text);


    }

    @Override
    public void traceCurrentMethodName() {
        StackTraceElement t = Thread.currentThread().getStackTrace()[2];
        String methodName = t.getMethodName();
        String className = t.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
        int lineNumber = t.getLineNumber();
        logger.info(className + "::" + methodName + ":" + lineNumber);
    }


    @Override
    public void dispose() {

    }


}
