package org.bwi4j.exception;

public class BwiException extends Exception {
    public BwiException(String message) {
        super(message);
    }

    public BwiException(String message, Throwable cause) {
        super(message, cause);
    }
}
