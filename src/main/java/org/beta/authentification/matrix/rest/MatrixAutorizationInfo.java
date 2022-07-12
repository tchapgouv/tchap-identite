/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest;

public class MatrixAutorizationInfo {
    private final String homeServer;
    private final Boolean authorized;

    public MatrixAutorizationInfo(String homeServer, Boolean authorized) {
        this.homeServer = homeServer;
        this.authorized = authorized;
    }

    public Boolean isAuthorized() {
        return authorized;
    }

    public String getHomeServer() {
        return homeServer;
    }
}
