package org.beta.tchap.identite.matrix.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.beta.tchap.identite.TestUtils.updateEnv;

class MatrixServiceIntTest {

    private static MatrixService matrixService;

    @BeforeAll
    public static void setup() throws Exception {
        updateEnv("TCHAP_IDENTITY_ACCOUNT","tchap-identite@tchap.beta.gouv.fr");
        updateEnv("TCHAP_IDENTITY_PASSWORD","TCLJsspN5A@6N@G3");
        updateEnv("TCHAP_HOME_SERVER_LIST", "https://matrix.i.tchap.gouv.fr,https://matrix.i.tchap.gouv.fr");

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


}
