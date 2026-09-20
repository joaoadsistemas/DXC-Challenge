package com.customer.system.domain.exception;

public class ScoreServiceException extends DomainException {

    public ScoreServiceException(String message) {
        super(message);
    }

    public ScoreServiceException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }
}
