/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.matrix.exception;

public class RoomDoesNotExist extends MatrixRuntimeException {

    public RoomDoesNotExist(Throwable e) {
        super(e);
    }
}
