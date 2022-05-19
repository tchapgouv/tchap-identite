package org.beta.tchap.identite.matrix.rest;

import org.apache.commons.lang.StringUtils;
import org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService;
import org.beta.tchap.identite.matrix.rest.login.LoginService;
import org.beta.tchap.identite.matrix.rest.user.UserInfoResource;
import org.beta.tchap.identite.matrix.rest.user.UserService;
import org.beta.tchap.identite.utils.Environment;
import org.jboss.logging.Logger;

import static org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService.buildHomeServerUrl;

public class MatrixService {

    private static final Logger LOG = Logger.getLogger(MatrixService.class);

    private final HomeServerService homeServerService;
    private final LoginService loginService;
    private final String account;
    private final String password;

    protected MatrixService() {
        account = Environment.getenv("TCHAP_ACCOUNT");
        password = Environment.getenv("TCHAP_PASSWORD");
        homeServerService = new HomeServerService();
        loginService = new LoginService();
    }

    public boolean isUserValid(String email) {
        LOG.infof("Check if email is valid in tchap : %s", email);
        if (StringUtils.isEmpty(email)) {
            return false;
        }

        String userHomeServer = homeServerService.findHomeServerByEmail(email);
        String accountHomeServerUrl = buildHomeServerUrl(homeServerService.findHomeServerByEmail(account));
        String accessToken = loginService.findAccessToken(accountHomeServerUrl, account, password);
        UserService userService = new UserService(accountHomeServerUrl, accessToken);
        UserInfoResource userInfoByEmail = userService.findUserInfoByEmail(email, userHomeServer);
        boolean result = userInfoByEmail != null && !userInfoByEmail.isDeactivated() && !userInfoByEmail.isExpired();
        LOG.infof("Email[%s] is valid in tchap : %s", email, result);
        return result;
    }
}
