package org.beta.tchap.identite.matrix.exception;
/**
 * Generic runtime exception when communciating with matrix
 */
public class MatrixRuntimeException extends RuntimeException {
    
    public MatrixRuntimeException(String message){
        super(message);
    }

    public MatrixRuntimeException(Throwable e){
        super(e);
    }
}
