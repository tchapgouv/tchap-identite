/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.tchap.identite.matrix.rest.homeserver;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;

public class HomeServerService {
    private final HomeServerClient homeServerClient;
    private final List<String> homeServerList;
    private static final String HOME_SERVER_URL_PREFIX = "https://matrix";
    private static final String DOMAIN_SEPARATOR = "@";

    public HomeServerService() {
        homeServerList =
                Arrays.asList(Environment.getenv(Constants.TCHAP_HOME_SERVER_LIST).split(","));
        homeServerClient = HomeServerClientFactory.build(getRandomHomeServerBaseUrl());
    }

    private String getRandomHomeServerBaseUrl() {
        String homeServerName = homeServerList.get(new Random().nextInt(homeServerList.size()));
        return buildHomeServerUrl(homeServerName);
    }

    public String findHomeServerByEmail(String email) {
        String domain = getDomain(email);
        HomeServerInfoResource homeServerInfoResource =
                homeServerClient.findHomeServerByEmail(new HomeServerInfoQuery("email", domain));
        return homeServerInfoResource.getHs();
    }

    private static String getDomain(String email) {
        return DOMAIN_SEPARATOR + StringUtils.substringAfter(email, DOMAIN_SEPARATOR);
    }

    public static String buildHomeServerUrl(String homeServerName) {
        return HOME_SERVER_URL_PREFIX + "." + homeServerName;
    }
}
