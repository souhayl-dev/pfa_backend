package com.bookingapp.domain.shared.exception;

/** A request that is well-formed but breaks a business rule, such as an invalid status change. */
public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
