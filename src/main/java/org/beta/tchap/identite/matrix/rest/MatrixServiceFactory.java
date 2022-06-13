package org.beta.tchap.identite.matrix.rest;

public class MatrixServiceFactory {
    private static MatrixService instance;

    public static MatrixService getInstance() {
        if (instance == null) {
            instance = new MatrixService();
        }
        return instance;
    }
}
