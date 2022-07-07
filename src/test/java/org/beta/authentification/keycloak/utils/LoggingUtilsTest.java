/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.utils;

import static org.beta.authentification.keycloak.utils.Constants.TCHAP_LOG_SENSITIVE_DATA;
import static org.beta.authentification.keycloak.utils.LoggingUtils.HIDDEN_DATA;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LoggingUtilsTest {

    String dataToLog = "test-_1@domain.gouv.fr";

    @BeforeAll
    public static void setup() {
        System.setProperty(TCHAP_LOG_SENSITIVE_DATA, "otherValue");
    }

    @Test
    void when_env_is_set_should_return_a_hash_with_special_char() {
        System.setProperty(TCHAP_LOG_SENSITIVE_DATA, "true");
        assertThat(new LoggingUtils().logOrHash(dataToLog), equalTo(dataToLog));

        // reset property
        System.setProperty(TCHAP_LOG_SENSITIVE_DATA, "otherValue");
    }

    @Test
    void when_env_is_not_set_should_not_return_a_hash() {
        String hash = "5af904c1c2d3290830968425e53601632be0ee9aba1ce84b877dad3dd0c21861";
        assertThat(new LoggingUtils().logOrHash(dataToLog), equalTo(hash));
    }

    @Test
    void when_env_is_set_should_return_hidden_data() {
        System.setProperty(TCHAP_LOG_SENSITIVE_DATA, "true");
        assertThat(new LoggingUtils().logOrHide(dataToLog), equalTo(dataToLog));

        // reset property
        System.setProperty(TCHAP_LOG_SENSITIVE_DATA, "otherThantrue");
    }

    @Test
    void when_env_is_set_should_return_data() {
        assertThat(new LoggingUtils().logOrHide(dataToLog), equalTo(HIDDEN_DATA));
    }
}
