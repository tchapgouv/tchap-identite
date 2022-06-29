package org.beta.tchap.identite.matrix.rest;

import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;

public class MatrixServiceFactory {
    private static MatrixService instance;

    public static MatrixService getInstance() {
        if (instance == null) {
            String accountEmail = Environment.getenv(Constants.TCHAP_ACCOUNT_EMAIL);
            String password = Environment.getenv(Constants.TCHAP_PASSWORD);
            instance = new MatrixService(accountEmail, password);
        }
        return instance;
    }
}
