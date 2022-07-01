package org.beta.tchap.identite.matrix.rest;

import org.apache.commons.lang.StringUtils;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;


public class MatrixServiceFactory {
    private static MatrixService instance;

    public static MatrixService getInstance() {
        if (instance == null) {
            String accountEmail = Environment.getenv(Constants.TCHAP_BOT_ACCOUNT_EMAIL);
            String password = Environment.getenv(Constants.TCHAP_BOT_PASSWORD);
            //String matrixId = Environment.getenv(Constants.TCHAP_BOT_MATRIX_ID);
            if (StringUtils.isEmpty(accountEmail) || StringUtils.isEmpty(password)) {
                throw new IllegalArgumentException(
                        "No account or password has been set. Please define the following"
                                + " environment variables : "
                                + Constants.TCHAP_BOT_ACCOUNT_EMAIL
                                + " and " + Constants.TCHAP_BOT_PASSWORD);
            }
            instance = new MatrixService(accountEmail, password);
        }
        return instance;
    }
}
