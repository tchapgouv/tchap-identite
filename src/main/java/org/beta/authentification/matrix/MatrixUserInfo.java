/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix;

public class MatrixUserInfo {
    private final String matrixId;
    private final Boolean valid;

    public MatrixUserInfo(String matrixId, Boolean valid) {
        this.matrixId = matrixId;
        this.valid = valid;
    }

    public Boolean isValid() {
        return valid;
    }

    public String getMatrixId() {
        return matrixId;
    }
}
