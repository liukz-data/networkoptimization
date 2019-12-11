package cn.gz.cm.networkoptimization.handler;

import org.apache.log4j.Logger;

public class ExceptionHandler {
    public static void exceptionHandler(Exception e, Logger logger) {
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            logger.error(stackTraceElement);
        }
    }
}