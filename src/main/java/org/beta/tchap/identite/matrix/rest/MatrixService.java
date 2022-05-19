package org.beta.tchap.identite.matrix.rest;

import org.apache.commons.lang.StringUtils;
import org.beta.tchap.identite.matrix.rest.homeserver.HomeServerClient;
import org.beta.tchap.identite.matrix.rest.homeserver.HomeServerClientFactory;
import org.beta.tchap.identite.matrix.rest.homeserver.HomeServerInfoQuery;
import org.beta.tchap.identite.matrix.rest.homeserver.HomeServerInfoResource;
import org.beta.tchap.identite.matrix.rest.login.LoginBody;
import org.beta.tchap.identite.matrix.rest.login.LoginClient;
import org.beta.tchap.identite.matrix.rest.login.LoginClientFactory;
import org.beta.tchap.identite.matrix.rest.login.LoginResource;
import org.beta.tchap.identite.matrix.rest.user.UserInfoResource;
import org.beta.tchap.identite.matrix.rest.user.UserService;
import org.beta.tchap.identite.utils.Environment;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MatrixService {

    private static final Logger LOG = Logger.getLogger(MatrixService.class);
    private static final String HOME_SERVER_URL_PREFIX = "https://matrix";

    private final HomeServerClient homeServerClient;
    private final String account;
    private final String password;
    private final List<String> homeServerList;

    protected MatrixService() {
        account = Environment.getenv("TCHAP_ACCOUNT");
        password = Environment.getenv("TCHAP_PASSWORD");
        homeServerList = Arrays.asList(Environment.getenv("TCHAP_HOME_SERVER_LIST").split(","));
        homeServerClient = HomeServerClientFactory.build(getRandomHomeServerBaseUrl());
    }

    private String getRandomHomeServerBaseUrl() {
        String homeServerName = homeServerList.get(new Random().nextInt(homeServerList.size()));
        return buildHomeServerUrl(homeServerName);
    }

    public boolean isUserValid(String email) {
        LOG.infof("Check if email is valid in tchap : %s", email);
        if (StringUtils.isEmpty(email)) {
            return false;
        }

        String userHomeServer = findHomeServerByEmail(email);
        String accountHomeServerUrl = buildHomeServerUrl(findHomeServerByEmail(account));
        String accessToken = findAccessToken(accountHomeServerUrl);

        UserService userService = new UserService(accountHomeServerUrl, accessToken);
        UserInfoResource userInfoByEmail = userService.findUserInfoByEmail(email, userHomeServer);
        boolean result = userInfoByEmail != null && !userInfoByEmail.isDeactivated() && !userInfoByEmail.isExpired();
        LOG.infof("Email[%s] is valid in tchap : %s", email, result);
        return result;
    }

    private String findHomeServerByEmail(String email) {
        HomeServerInfoResource homeServerInfoResource = homeServerClient.findHomeServerByEmail(new HomeServerInfoQuery("email", email));
        return homeServerInfoResource.getHs();
    }

    private String findAccessToken(String homeServerUrl) {
        LoginResource loginResource = login(homeServerUrl);
        return loginResource.getAccessToken();
    }

    private LoginResource login(String homeServerUrl) {
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
        return client.login(body);
    }

    private String buildHomeServerUrl(String homeServerName) {
        return HOME_SERVER_URL_PREFIX + "." + homeServerName;
    }
}
