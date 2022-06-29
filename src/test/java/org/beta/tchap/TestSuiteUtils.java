package org.beta.tchap;

import io.github.cdimascio.dotenv.Dotenv;

public class TestSuiteUtils {

    public static final String ENV_DELETE_ROOM_AFTER_TESTS = "TEST_DELETE_ROOM_AFTER_TESTS";
    public static String ENV_TEST_BOT_TO_USER_MID = "TEST_BOT_TO_USER_MID";
    public static String TEST_USER2_ACCOUNT = "TEST_USER2_ACCOUNT";
    public static String TEST_USER2_PASSWORD = "TEST_USER2_PASSWORD";

    public static void loadEnvFromDotEnvFile(){
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
    }
}
