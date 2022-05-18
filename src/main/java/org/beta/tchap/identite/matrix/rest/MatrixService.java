package org.beta.tchap.identite.matrix.rest;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.beta.tchap.identite.matrix.rest.gson.GsonFactory;
import org.beta.tchap.identite.matrix.rest.login.LoginBody;
import org.beta.tchap.identite.matrix.rest.login.LoginClient;
import org.beta.tchap.identite.matrix.rest.login.LoginClientFactory;
import org.beta.tchap.identite.matrix.rest.login.LoginResource;
import org.beta.tchap.identite.matrix.rest.user.UserInfoResource;
import org.beta.tchap.identite.matrix.rest.user.UserService;

public class MatrixService {

    // check if we need to incorporate other servers
    public static final String MATRIX_HOME_SERVER = "i.tchap.gouv.fr";
    public static final String MATRIX_BASE_URL = "https://matrix." + MATRIX_HOME_SERVER;

    private final UserService userService;

    public MatrixService() {
        Gson gson = GsonFactory.build();
        LoginResource loginResource = login(gson);
        userService = new UserService(gson,loginResource);
    }

    private LoginResource login(Gson gson) {
        LoginClient client = LoginClientFactory.build(gson);

        String account = System.getProperty("TCHAP_IDENTITY_ACCOUNT");
        String password = System.getProperty("TCHAP_IDENTITY_PASSWORD");

        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)){
            throw new IllegalArgumentException("No account or password has been set. Please define the following" +
                    " environment variables : TCHAP_IDENTITY_ACCOUNT and TCHAP_IDENTITY_PASSWORD");
        }

        LoginBody body = new LoginBody();
        body.setType("m.login.password");
        body.setAddress(account);
        body.setPassword(password);
        body.setMedium("email");
        return client.login(body);
    }

    public boolean isUserValid(String email) {
        if (StringUtils.isEmpty(email)){
            return false;
        }
        UserInfoResource userInfoByEmail = userService.findUserInfoByEmail(email);
        return userInfoByEmail != null && !userInfoByEmail.isDeactivated() && !userInfoByEmail.isExpired();
    }

}
