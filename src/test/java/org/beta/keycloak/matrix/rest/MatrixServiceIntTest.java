/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.matrix.rest;

import org.beta.keycloak.TestSuiteUtils;
import org.beta.keycloak.matrix.MatrixUserInfo;
import org.beta.keycloak.matrix.rest.MatrixService;
import org.beta.keycloak.matrix.rest.MatrixServiceFactory;
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

    @Test
    void shouldFindMatrixUserInfoBeValid() {
        MatrixUserInfo accountValidOnTchap =
                matrixService.findMatrixUserInfo(
                        "i.tchap.gouv.fr", "maghen.calinghee@beta.gouv.fr");
        Assertions.assertTrue(accountValidOnTchap.isValid());
        Assertions.assertEquals(
                "@maghen.calinghee-beta.gouv.fr:i.tchap.gouv.fr",
                accountValidOnTchap.getMatrixId());
    }

    @Test
    void shouldFindMatrixUserInfoNotBeValid() {
        MatrixUserInfo accountValidOnTchap =
                matrixService.findMatrixUserInfo("i.tchap.gouv.fr", "clark.kent@beta.gouv.fr");
        Assertions.assertFalse(accountValidOnTchap.isValid());
        Assertions.assertEquals(null, accountValidOnTchap.getMatrixId());
    }
}
