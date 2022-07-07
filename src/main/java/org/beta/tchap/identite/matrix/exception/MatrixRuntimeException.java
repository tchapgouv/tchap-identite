/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.tchap.identite.matrix.exception;
/** Generic runtime exception when communciating with matrix */
public class MatrixRuntimeException extends RuntimeException {
    
    public MatrixRuntimeException(String message){
        super(message);
    }

    public MatrixRuntimeException(Throwable e){
        super(e);
    }
}
