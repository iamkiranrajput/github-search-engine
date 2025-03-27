package com.guardians.gse.exception;

public class DatabaseOperationFailedException extends RuntimeException {
    public DatabaseOperationFailedException(String msg) {
        super(msg);
    }
}
