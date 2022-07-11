/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.bot;

import org.beta.authentification.matrix.rest.MatrixServiceFactory;

public class BotSenderFactory {
    private static BotSender instance;

    public static BotSender getInstance() {
        if (instance == null) {
            instance = new BotSender(MatrixServiceFactory.getAuthenticatedInstance());
        }
        return instance;
    }
}
