/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest;

import java.util.List;

import org.beta.authentification.keycloak.utils.Constants;
import org.beta.authentification.keycloak.utils.Environment;

public class MatrixServiceUtil {

    /**
     * This method should be used only for tests, in all other cases use the Factory {@link MatrixServiceFactory}
     * @param accountEmail
     * @param tchapToken
     * @return
     */
    public static MatrixService getMatrixService(String accountEmail, String tchapToken) {

        List<String> homeServerList = Environment.strToList(Environment.getenv(Constants.TCHAP_HOME_SERVER_LIST));
        List<String>  unauthorizedList = Environment.strToList(Environment.getenv(Constants.TCHAP_UNAUTHORIZED_HOME_SERVER_LIST));


        return new MatrixService(accountEmail, homeServerList, unauthorizedList, tchapToken);
    }
}
