/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.matrix.rest;


public class MatrixServiceUtil {

    public static MatrixService getMatrixService(String accountEmail, String tchapPassword) {
        return new MatrixService(accountEmail, tchapPassword);
    }
}
