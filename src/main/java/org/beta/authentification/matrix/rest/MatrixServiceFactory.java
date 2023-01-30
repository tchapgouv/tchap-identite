/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.beta.authentification.keycloak.utils.Constants;
import org.beta.authentification.keycloak.utils.Environment;
import org.beta.authentification.keycloak.utils.Features;

/**
 * This factory should not be used as the use of singleton and statefull instances of MatrixService are being dropped
 */
@Deprecated
public class MatrixServiceFactory {
    private static MatrixService instance;
    private static MatrixService authenticatedInstance;

    public static MatrixService getInstance() {
        if (Features.isMatrixServiceReuseEnabled()) {
            return getSingletonInstance();
        }
        return getNonSingletonInstance();
    }

    private static MatrixService getSingletonInstance() {
        if (instance == null) {
            instance = getNonSingletonInstance();
        }
        return instance;
    }

    private static MatrixService getNonSingletonInstance() {
        List<String> homeServerList = Environment.strToList(Environment.getenv(Constants.TCHAP_HOME_SERVER_LIST));
        List<String>  unauthorizedList = Environment.strToList(Environment.getenv(Constants.TCHAP_UNAUTHORIZED_HOME_SERVER_LIST));
        return new MatrixService(homeServerList, unauthorizedList);
    }

    public static MatrixService getAuthenticatedInstance() {
        if (Features.isMatrixServiceReuseEnabled()) {
            return getSingletonAuthenticatedInstance();
        }
        return getNonSingletonAuthenticatedInstance();
    }

    private static MatrixService getSingletonAuthenticatedInstance() {
        if (authenticatedInstance == null) {
            authenticatedInstance = getNonSingletonAuthenticatedInstance();
        }
        return authenticatedInstance;
    }

    private static MatrixService getNonSingletonAuthenticatedInstance() {
        String accountEmail = Environment.getenv(Constants.TCHAP_BOT_ACCOUNT_EMAIL);
        String token = Environment.getenv(Constants.TCHAP_BOT_TOKEN);
        List<String> homeServerList = Environment.strToList(Environment.getenv(Constants.TCHAP_HOME_SERVER_LIST));
        List<String>  unauthorizedList = Environment.strToList(Environment.getenv(Constants.TCHAP_UNAUTHORIZED_HOME_SERVER_LIST));

        if (StringUtils.isEmpty(accountEmail) || StringUtils.isEmpty(token)) {
            throw new IllegalArgumentException(
                    "No account or token has been set. Please define the following"
                            + " environment variables : "
                            + Constants.TCHAP_BOT_ACCOUNT_EMAIL
                            + " and "
                            + Constants.TCHAP_BOT_TOKEN);
        }
        return new MatrixService(accountEmail, homeServerList, unauthorizedList, token);
    }
}
