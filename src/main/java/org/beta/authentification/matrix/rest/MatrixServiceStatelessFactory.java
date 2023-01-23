/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest;

import org.apache.commons.lang.StringUtils;
import org.beta.authentification.keycloak.utils.Constants;
import org.beta.authentification.keycloak.utils.Environment;
import java.util.List;

public class MatrixServiceStatelessFactory {

    //TODO: not used yet
    public static MatrixService getStatelessInstanceWithToken(String accountEmail, String accessToken) {
        List<String> homeServerList = Environment.strToList(Environment.getenv(Constants.TCHAP_HOME_SERVER_LIST));
        List<String>  unauthorizedList = Environment.strToList(Environment.getenv(Constants.TCHAP_UNAUTHORIZED_HOME_SERVER_LIST));

        if (StringUtils.isEmpty(accountEmail) || StringUtils.isEmpty(accessToken)) {
            throw new IllegalArgumentException(
                    "No account or accessToken has been set. Please define the following"
                            + " environment variables : "
                            + Constants.TCHAP_BOT_ACCOUNT_EMAIL
                            + " and "
                            //todo + Constants.TCHAP_BOT_PASSWORD
            );
        }
        return new MatrixService(accountEmail, homeServerList, unauthorizedList, accessToken);
    }


    /**
     * This is NOT a stateless method as the instance will login with login/password and store the acess token
     * @param accountEmail
     * @param password
     * @return
     */
        public static MatrixService getInstanceWithPassword(String accountEmail, String password) {
        List<String> homeServerList = Environment.strToList(Environment.getenv(Constants.TCHAP_HOME_SERVER_LIST));
        List<String>  unauthorizedList = Environment.strToList(Environment.getenv(Constants.TCHAP_UNAUTHORIZED_HOME_SERVER_LIST));

        if (StringUtils.isEmpty(accountEmail) || StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException(
                    "No account or password has been set. Please define the following"
                            + " environment variables : "
                            + Constants.TCHAP_BOT_ACCOUNT_EMAIL
                            + " and "
                            + Constants.TCHAP_BOT_PASSWORD);
        }
        return new MatrixService(accountEmail, password, homeServerList, unauthorizedList);
    }
}
