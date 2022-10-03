package com.example.allviddownloader.utils;

public class ProgressException extends Exception {

    private ErrorCause error;

    public ProgressException(ErrorCause error) {
        this.error = error;
    }

    public ErrorCause getError() {
        return error;
    }

    @Override
    public String toString() {
        return error.toString();
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String getLocalizedMessage() {
        return toString();
    }
}
