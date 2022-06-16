package org.beta.tchap;

import io.github.cdimascio.dotenv.Dotenv;

public class TestSuiteUtils {

    public static void loadEnvFromDotEnvFile(){
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
    }
}
