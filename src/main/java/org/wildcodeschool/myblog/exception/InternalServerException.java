package org.wildcodeschool.myblog.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException() {
        super("Une erreur interne est survenue");
    }
}
