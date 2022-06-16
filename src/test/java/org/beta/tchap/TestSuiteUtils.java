package org.beta.tchap;

import io.github.cdimascio.dotenv.Dotenv;

public class TestSuiteUtils {

    public static String ENV_TEST_BOT_TO_USER_MID = "TEST_BOT_TO_USER_MID";

    public static void loadEnvFromDotEnvFile(){
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
    }
}
