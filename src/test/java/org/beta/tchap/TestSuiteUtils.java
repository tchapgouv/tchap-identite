/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.tchap;

import io.github.cdimascio.dotenv.Dotenv;
import org.jboss.resteasy.logging.Logger;

public class TestSuiteUtils {
    private static final Logger LOG = Logger.getLogger(TestSuiteUtils.class);

    public static final String ENV_DELETE_ROOM_AFTER_TESTS = "TEST_DELETE_ROOM_AFTER_TESTS";
    public static String TEST_BOT_TO_USER_MID = "TEST_BOT_TO_USER_MID";
    public static String TEST_USER2_ACCOUNT = "TEST_USER2_ACCOUNT";
    public static String TEST_USER2_PASSWORD = "TEST_USER2_PASSWORD";
    public static String TEST_USER2_MATRIXID = "TEST_USER2_MATRIXID";

    public static void loadEnvFromDotEnvFile() {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
    }

    public static void waitAbit() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void wait2second() {
        try {
            LOG.warn("Waiting 2 seconds...");
            Thread.sleep(2000);
            LOG.warn("End of waiting, back to work!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
