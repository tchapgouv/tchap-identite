package org.beta.tchap.identite.matrix.rest.login;

import org.apache.commons.lang.StringUtils;

public class LoginService {

    public String findAccessToken(String homeServerUrl, String account, String password) {
        LoginClient client = LoginClientFactory.build(homeServerUrl);

        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("No account or password has been set. Please define the following" +
                    " environment variables : TCHAP_ACCOUNT and TCHAP_PASSWORD");
        }

        LoginBody body = new LoginBody();
        body.setType("m.login.password");
        body.setAddress(account);
        body.setPassword(password);
        body.setMedium("email");
        LoginResource loginResource = client.login(body);
        return loginResource.getAccessToken();
    }
}