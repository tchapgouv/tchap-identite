/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.matrix.rest.homeserver;

public class HomeServerInfoQuery {
    private final String medium;
    private final String address;

    public HomeServerInfoQuery(String medium, String address) {
        this.medium = medium;
        this.address = address;
    }

    public String getMedium() {
        return medium;
    }

    public String getAddress() {
        return address;
    }
}
