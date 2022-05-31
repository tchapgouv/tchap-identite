package org.beta.tchap.identite.matrix.rest;

import org.apache.commons.lang.StringUtils;
import org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService;
import org.beta.tchap.identite.matrix.rest.login.LoginService;
import org.beta.tchap.identite.matrix.rest.user.UserInfoResource;
import org.beta.tchap.identite.matrix.rest.user.UserService;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;
import org.beta.tchap.identite.utils.LoggingUtilsFactory;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService.buildHomeServerUrl;

public class MatrixService {

    private static final Logger LOG = Logger.getLogger(MatrixService.class);

    private final HomeServerService homeServerService;
    private final LoginService loginService;
    private final String account;
    private final String password;

    protected MatrixService() {
        account = Environment.getenv(Constants.TCHAP_ACCOUNT);
        password = Environment.getenv(Constants.TCHAP_PASSWORD);
        homeServerService = new HomeServerService();
        loginService = new LoginService();
    }

    public boolean isUserValid(String email) {
        if(LOG.isDebugEnabled()){
            LOG.debugf("Check if email is valid in tchap : %s", LoggingUtilsFactory.getInstance().logOrHash(email));
        }
        if (StringUtils.isEmpty(email)) {
            return false;
        }

        String userHomeServer = homeServerService.findHomeServerByEmail(email);
        boolean isValid = isEmailAcceptedOnTchap(userHomeServer);
        if(LOG.isDebugEnabled()){
            LOG.debugf("Email[%s] is valid in tchap : %s", LoggingUtilsFactory.getInstance().logOrHash(email), isValid);
        }
        return isValid;
    }

    /**
     * Check if an email is accepted on Tchap based on an hardcorded domain list
     */
    private boolean isEmailAcceptedOnTchap(String userHomeServer) {
        return !getInvalidHomeServers().contains(userHomeServer);
    }

    private List<String> getInvalidHomeServers() {
        String unauthorizedList = Environment.getenv(Constants.TCHAP_UNAUTHORIZED_HOME_SERVER_LIST);
        return StringUtils.isNotEmpty(unauthorizedList) ?
                Arrays.asList(unauthorizedList.split(","))
                : Collections.emptyList();
    }

    /**
     * Check if an account has been created and still valid in Tchap
     *
     * TODO : this method is not used anymore. This method is an example of an authenticated flow with Tchap.
     * The acesss token should be cached if this flow is used.
     *
     */
    private boolean isAccountValidOnTchap(String email, String userHomeServer) {
        String accountHomeServerUrl = buildHomeServerUrl(homeServerService.findHomeServerByEmail(account));
        String accessToken = loginService.findAccessToken(accountHomeServerUrl, account, password);
        UserService userService = new UserService(accountHomeServerUrl, accessToken);
        UserInfoResource userInfoByEmail = userService.findUserInfoByEmail(email, userHomeServer);
        return userInfoByEmail != null && !userInfoByEmail.isDeactivated() && !userInfoByEmail.isExpired();
    }
}
