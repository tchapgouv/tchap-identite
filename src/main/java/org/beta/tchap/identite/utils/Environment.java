package org.beta.tchap.identite.utils;

public class Environment {

    public static String getenv(String key) {
        String result = System.getenv(key);
        if (result == null) {
            return System.getProperty(key);
        }
        return result;
    }
}
