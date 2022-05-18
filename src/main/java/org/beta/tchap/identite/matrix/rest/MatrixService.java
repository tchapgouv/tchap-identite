package org.beta.tchap.identite.matrix.rest;

import org.apache.commons.lang.StringUtils;
import org.beta.tchap.identite.matrix.rest.login.LoginBody;
import org.beta.tchap.identite.matrix.rest.login.LoginClient;
import org.beta.tchap.identite.matrix.rest.login.LoginClientFactory;
import org.beta.tchap.identite.matrix.rest.login.LoginResource;
import org.beta.tchap.identite.matrix.rest.user.UserInfoResource;
import org.beta.tchap.identite.matrix.rest.user.UserService;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MatrixService {

    private static final Logger LOG = Logger.getLogger(MatrixService.class);

    // check if we need to incorporate other servers
    /*public static final String MATRIX_HOME_SERVER = "i.tchap.gouv.fr";
    public static final String MATRIX_BASE_URL = "https://matrix." + MATRIX_HOME_SERVER;
*/

    private final UserService userService;
    private String account;
    private String password;
    private List<String> homeServerList;

    protected MatrixService() {
        LoginResource loginResource = login();
        userService = new UserService(loginResource, homeServerList);
        account = System.getenv("TCHAP_IDENTITY_ACCOUNT");
        password = System.getenv("TCHAP_IDENTITY_PASSWORD");
        homeServerList = Arrays.asList(System.getenv("TCHAP_HOME_SERVER_LIST").split(","));
    }

    private LoginResource login() {
        LoginClient client = LoginClientFactory.build(homeServerList.get(new Random().nextInt(homeServerList.size())));

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
        LOG.infof("Check if email is valid in tchap : %s", email);
        if (StringUtils.isEmpty(email)){
            return false;
        }
        UserInfoResource userInfoByEmail = userService.findUserInfoByEmail(email);
        boolean result = userInfoByEmail != null && !userInfoByEmail.isDeactivated() && !userInfoByEmail.isExpired();
        LOG.infof("Email[%s] is valid in tchap : %s", email, result);
        return result;
    }
}
