/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.matrix.rest.user;

import org.beta.keycloak.matrix.rest.login.LoginResource;
import org.beta.keycloak.matrix.rest.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RoomServiceTest {

    private static UserService userService;

    @BeforeAll
    public static void setup() {
        LoginResource loginResource = new LoginResource();
        //        loginResource.setAccessToken("fakeToken");
        userService =
                new UserService("https://matrix.i.tchap.gouv.fr", loginResource.getAccessToken());
    }

    @Test
    void emailToUserId() {
        String userId = UserService.emailToUserId("clark.kent@beta.gouv.fr", "i.tchap.gouv.fr");
        Assertions.assertEquals("@clark.kent-beta.gouv.fr:i.tchap.gouv.fr", userId);
    }
}
