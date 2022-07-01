/*
 * Copyright (c) 2022. DINUM
 */

package org.beta.tchap.identite.matrix.rest;

public class MatrixServiceUtil {

    static public MatrixService getMatrixService(String accountEmail, String tchapPassword){
        return new MatrixService(accountEmail, tchapPassword);
    }

}
