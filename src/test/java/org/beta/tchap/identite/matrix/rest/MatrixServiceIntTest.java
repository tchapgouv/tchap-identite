package org.beta.tchap.identite.matrix.rest;

import org.beta.tchap.TestSuiteUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MatrixServiceIntTest {

    private static MatrixService matrixService;

    @BeforeAll
    public static void setup() {
        TestSuiteUtils.loadEnvFromDotEnvFile();
        Assertions.assertTrue(!System.getProperty("TCHAP_HOME_SERVER_LIST").isEmpty());
        Assertions.assertTrue(!System.getProperty("TCHAP_SKIP_CERTIFICATE_VALIDATION").isEmpty());
        Assertions.assertTrue(!System.getProperty("TCHAP_UNAUTHORIZED_HOME_SERVER_LIST").isEmpty());
        
        matrixService = MatrixServiceFactory.getInstance();
    }

    @Test
    void shouldExistingTchapUserBeValid() {
        boolean userValid = matrixService.isUserValid("maghen.calinghee@beta.gouv.fr");
        Assertions.assertTrue(userValid);
    }

    @Test
    void shouldNonExistingTchapUserBeValid() {
        boolean userValid = matrixService.isUserValid("clark.kent@beta.gouv.fr");
        Assertions.assertTrue(userValid);
    }

    @Test
    void shouldEducationUserBeValid() {
        boolean userValid = matrixService.isUserValid("clark.kent@ac-corse.fr");
        Assertions.assertTrue(userValid);
    }

    @Test
    void shouldExternalUserNotBeValid() {
        boolean userValid = matrixService.isUserValid("clark.kent@gmail.com");
        Assertions.assertFalse(userValid);
    }

    @Test
    void shouldCommunityUserNotBeValid() {
        boolean userValid = matrixService.isUserValid("clark.kent@3t-chatellerault.fr");
        Assertions.assertFalse(userValid);
    }

    @Test
    void shouldUserWithInvalidEmailNotBeValid() {
        boolean userValid = matrixService.isUserValid("clark.kent");
        Assertions.assertFalse(userValid);
    }
}
