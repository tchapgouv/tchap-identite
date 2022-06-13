package org.beta.tchap.identite.utils;

public class SecureCodeFactory {
    private static SecureCode instance;

    public static SecureCode getInstance() {
        if (instance == null) {
            instance = new SecureCode();
        }
        return instance;
    }
}
