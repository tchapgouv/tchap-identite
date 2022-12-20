/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.utils;

import org.keycloak.models.ClientModel;

public class Features {
    public static boolean isTchapBotEnabled(ClientModel clientModel) {
        return Boolean.parseBoolean(clientModel.getAttribute(Constants.FEATURE_TCHAP_BOT_OTP));
    }

    public static boolean isHomeServerSelectionStrategyEnabled() {
        return Boolean.parseBoolean(Environment.getenv(Constants.FEATURE_HEALTHY_HOME_SERVER_STRATEGY));
    }

    public static boolean isMatrixServiceReuseEnabled() {
        return Boolean.parseBoolean(Environment.getenv(Constants.FEATURE_REUSE_MATRIX_SERVICE));
    }
}
