package org.beta.tchap.identite.utils;

public class LoggingUtilsFactory {
    private static LoggingUtils instance;

    public static LoggingUtils getInstance() {
        if ( instance == null ){
            instance = new LoggingUtils();
        }
        return instance;
    }
}
