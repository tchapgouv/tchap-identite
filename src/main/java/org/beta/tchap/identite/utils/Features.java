/*
 * Copyright (c) 2022. DINUM
 */

package org.beta.tchap.identite.utils;

public class Features {
    public static boolean isTchapBotEnabled() {
        return Boolean.parseBoolean(Environment.getenv(Constants.FEATURE_TCHAP_BOT_OTP));
    }
}
