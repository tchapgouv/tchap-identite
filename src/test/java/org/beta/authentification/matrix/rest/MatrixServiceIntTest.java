/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest;

import org.beta.authentification.keycloak.TestSuiteUtils;
import org.beta.authentification.keycloak.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.beta.authentification.keycloak.utils.Environment;

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
        MatrixAutorizationInfo matrixAutorizationInfo = matrixService.isEmailAuthorized("maghen.calinghee@beta.gouv.fr");
        Assertions.assertTrue(matrixAutorizationInfo.isAuthorized());
    }

    @Test
    void shouldNonExistingTchapUserBeValid() {
        MatrixAutorizationInfo matrixAutorizationInfo = matrixService.isEmailAuthorized("clark.kent@beta.gouv.fr");
        Assertions.assertTrue(matrixAutorizationInfo.isAuthorized());
    }

    @Test
    void shouldEducationUserBeValid() {
        MatrixAutorizationInfo matrixAutorizationInfo = matrixService.isEmailAuthorized("clark.kent@ac-corse.fr");
        Assertions.assertTrue(matrixAutorizationInfo.isAuthorized());
    }

    @Test
    void shouldExternalUserNotBeValid() {
        MatrixAutorizationInfo matrixAutorizationInfo = matrixService.isEmailAuthorized("clark.kent@gmail.com");
        Assertions.assertFalse(matrixAutorizationInfo.isAuthorized());
    }

    @Test
    void shouldCommunityUserNotBeValid() {
        MatrixAutorizationInfo matrixAutorizationInfo = matrixService.isEmailAuthorized("clark.kent@3t-chatellerault.fr");
        Assertions.assertFalse(matrixAutorizationInfo.isAuthorized());
    }

    @Test
    void shouldUserWithInvalidEmailNotBeValid() {
        MatrixAutorizationInfo matrixAutorizationInfo = matrixService.isEmailAuthorized("clark.kent");
        Assertions.assertFalse(matrixAutorizationInfo.isAuthorized());
    }

    @Test
    void shouldFindMatrixUserInfoBeValid() {
        matrixService = MatrixServiceFactory.getAuthenticatedInstance(Environment.getenv(Constants.TCHAP_BOT_ACCOUNT_EMAIL), Environment.getenv(Constants.TCHAP_BOT_PASSWORD));
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
        matrixService = MatrixServiceFactory.getAuthenticatedInstance(Environment.getenv(Constants.TCHAP_BOT_ACCOUNT_EMAIL), Environment.getenv(Constants.TCHAP_BOT_PASSWORD));
        MatrixUserInfo accountValidOnTchap =
                matrixService.findMatrixUserInfo("i.tchap.gouv.fr", "clark.kent@beta.gouv.fr");
        Assertions.assertFalse(accountValidOnTchap.isValid());
        Assertions.assertEquals(null, accountValidOnTchap.getMatrixId());
    }
}
