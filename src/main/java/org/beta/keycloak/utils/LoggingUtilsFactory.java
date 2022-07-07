/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.utils;

public class LoggingUtilsFactory {
    private static LoggingUtils instance;

    public static LoggingUtils getInstance() {
        if (instance == null) {
            instance = new LoggingUtils();
        }
        return instance;
    }
}
