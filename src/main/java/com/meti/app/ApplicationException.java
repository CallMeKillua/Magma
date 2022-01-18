package com.meti.app;

public class ApplicationException extends Exception {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Exception cause) {
        super(cause);
    }

    public ApplicationException(String message, Exception cause) {
        super(message, cause);
    }
}