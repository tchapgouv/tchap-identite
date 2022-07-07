/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.utils;

import com.google.common.hash.Hashing;

import static org.beta.keycloak.utils.Constants.TCHAP_LOG_SENSITIVE_DATA;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class LoggingUtils {

    Boolean logSensitiveData;
    public static String HIDDEN_DATA = "******";

    LoggingUtils() {
        String sensitive_data_env = Environment.getenv(TCHAP_LOG_SENSITIVE_DATA);
        this.logSensitiveData =
                sensitive_data_env != null
                        && "true".equals(sensitive_data_env.toLowerCase(Locale.ROOT));
    }

    public String logOrHide(String data) {
        if (logSensitiveData) {
            return data;
        } else {
            return HIDDEN_DATA;
        }
    }

    public String logOrHash(String data) {
        if (logSensitiveData) {
            return data;
        }
        return Hashing.sha256().hashString(data, StandardCharsets.UTF_8).toString();
    }
}
