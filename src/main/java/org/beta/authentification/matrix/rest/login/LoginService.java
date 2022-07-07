/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest.login;

public class LoginService {

    public String findAccessToken(String homeServerUrl, String account, String password) {
        LoginClient client = LoginClientFactory.build(homeServerUrl);

        LoginBody body = new LoginBody();
        body.setType("m.login.password");
        body.setAddress(account);
        body.setPassword(password);
        body.setMedium("email");
        LoginResource loginResource = client.login(body);
        return loginResource.getAccessToken();
    }
}
