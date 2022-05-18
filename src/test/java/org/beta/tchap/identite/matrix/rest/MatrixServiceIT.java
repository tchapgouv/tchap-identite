package org.beta.tchap.identite.matrix.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class MatrixServiceIntTest {

    private static MatrixService matrixService;

    @BeforeAll
    public static void setup() throws Exception {
        System.setProperty("TCHAP_IDENTITY_ACCOUNT","tchap-identite@tchap.beta.gouv.fr");
        System.setProperty("TCHAP_IDENTITY_PASSWORD","TCLJsspN5A@6N@G3");
        System.setProperty("TCHAP_HOME_SERVER_LIST", "i.tchap.gouv.fr,e.tchap.gouv.fr");

        matrixService = new MatrixService();
    }

    @Test
    void shouldUserBeValid() {
        boolean userValid = matrixService.isUserValid("maghen.calinghee@beta.gouv.fr");
        Assertions.assertTrue(userValid);
    }

    @Test
    void shouldUserNotBeValid() {
        boolean userValid = matrixService.isUserValid("clark.kent@beta.gouv.fr");
        Assertions.assertFalse(userValid);
    }

    @Test
    void shouldExternalUserNotBeValid() {
        boolean userValid = matrixService.isUserValid("clark.kent@gmail.com");
        Assertions.assertFalse(userValid);
    }


}
