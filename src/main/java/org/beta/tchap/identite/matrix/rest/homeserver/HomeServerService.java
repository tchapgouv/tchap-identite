package org.beta.tchap.identite.matrix.rest.homeserver;

import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HomeServerService {
    private final HomeServerClient homeServerClient;
    private final List<String> homeServerList;
    private static final String HOME_SERVER_URL_PREFIX = "https://matrix";

    public HomeServerService(){
        homeServerList = Arrays.asList(Environment.getenv(Constants.TCHAP_HOME_SERVER_LIST).split(","));
        homeServerClient = HomeServerClientFactory.build(getRandomHomeServerBaseUrl());
    }

    private String getRandomHomeServerBaseUrl() {
        String homeServerName = homeServerList.get(new Random().nextInt(homeServerList.size()));
        return buildHomeServerUrl(homeServerName);
    }

    public String findHomeServerByEmail(String email) {
        HomeServerInfoResource homeServerInfoResource = homeServerClient.findHomeServerByEmail(new HomeServerInfoQuery("email", email));
        return homeServerInfoResource.getHs();
    }

    public static String buildHomeServerUrl(String homeServerName) {
        return HOME_SERVER_URL_PREFIX + "." + homeServerName;
    }
}
