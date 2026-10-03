package com.bookingapp.domain.shared.exception;

public class UnauthorizedActionException extends DomainException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
