package org.beta.tchap.identite.matrix.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.beta.tchap.identite.TestUtils.updateEnv;

class MatrixServiceIntTest {

    private static MatrixService matrixService;

    @BeforeAll
    public static void setup() throws Exception {
        updateEnv("TCHAP_IDENTITY_ACCOUNT","<replace here with the appropriate service email account>");
        updateEnv("TCHAP_IDENTITY_PASSWORD","<get the password>");
        Map map = new HashMap();
        //setEnv(map);
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
