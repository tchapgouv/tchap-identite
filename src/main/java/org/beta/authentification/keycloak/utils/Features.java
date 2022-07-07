/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.utils;

public class Features {
    public static boolean isTchapBotEnabled() {
        return Boolean.parseBoolean(Environment.getenv(Constants.FEATURE_TCHAP_BOT_OTP));
    }
}
