/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.bot;

import org.beta.authentification.matrix.rest.MatrixService;

public class BotSenderFactory {

    public static BotSender getInstance(MatrixService matrixService) {
        return new BotSender(matrixService);
    }
}
