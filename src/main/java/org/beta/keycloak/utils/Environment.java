/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.utils;

public class Environment {

    public static String getenv(String key) {
        String result = System.getenv(key);
        if (result == null) {
            return System.getProperty(key);
        }
        return result;
    }
}
