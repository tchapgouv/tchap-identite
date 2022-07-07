/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.exception;

public class UserDoesNotExist extends MatrixRuntimeException {

    public UserDoesNotExist(Throwable e) {
        super(e);
    }
}
