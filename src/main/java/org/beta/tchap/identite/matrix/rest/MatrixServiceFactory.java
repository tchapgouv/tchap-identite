package org.beta.tchap.identite.matrix.rest;

import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;

public class MatrixServiceFactory {
    private static MatrixService instance;

    public static MatrixService getInstance() {
        if (instance == null) {
            String accountEmail = Environment.getenv(Constants.TCHAP_BOT_ACCOUNT_EMAIL);
            String password = Environment.getenv(Constants.TCHAP_BOT_PASSWORD);
            //String matrixId = Environment.getenv(Constants.TCHAP_BOT_MATRIX_ID);
            instance = new MatrixService(accountEmail, password);
        }
        return instance;
    }
}
