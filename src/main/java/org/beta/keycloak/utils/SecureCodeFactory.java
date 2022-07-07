/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.utils;

public class SecureCodeFactory {
    private static SecureCode instance;

    public static SecureCode getInstance() {
        if (instance == null) {
            instance = new SecureCode();
        }
        return instance;
    }
}
