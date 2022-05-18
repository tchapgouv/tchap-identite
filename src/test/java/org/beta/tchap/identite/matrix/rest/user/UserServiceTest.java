package org.beta.tchap.identite.matrix.rest.user;

import org.beta.tchap.identite.matrix.rest.login.LoginResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class UserServiceTest {

    private static UserService userService;

    @BeforeAll
    public static void setup(){
        LoginResource loginResource = new LoginResource();
        loginResource.setAccessToken("fakeToken");
        userService = new UserService(loginResource, Arrays.asList("https://matrix.i.tchap.gouv.fr"));
    }


    @Test
    void emailToUserId() {
        String userId = userService.emailToUserId("clark.kent@beta.gouv.fr");
        Assertions.assertEquals("@clark.kent-beta.gouv.fr:i.tchap.gouv.fr",userId);
    }
}
