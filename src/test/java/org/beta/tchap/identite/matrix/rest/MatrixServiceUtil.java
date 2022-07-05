/*
 * Copyright (c) 2022. DINUM
 * This·file·is·licensed·under·the·MIT·License,·see·LICENSE.md
 */

package org.beta.tchap.identite.matrix.rest;

public class MatrixServiceUtil {

    static public MatrixService getMatrixService(String accountEmail, String tchapPassword){
        return new MatrixService(accountEmail, tchapPassword);
    }

}
