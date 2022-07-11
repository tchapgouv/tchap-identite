/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Environment {

    public static String getenv(String key) {
        String result = System.getenv(key);
        if (result == null) {
            return System.getProperty(key);
        }
        return result;
    }

    public static List<String> strToList(String envVar) {
        return StringUtils.isNotEmpty(envVar)
                ? Arrays.asList(envVar.split(","))
                : Collections.emptyList();
    }
}
