package org.wildcodeschool.myblog.exception;

public class AlreadyUseException extends RuntimeException {
    public AlreadyUseException(String message) {
        super(message);
    }
}
