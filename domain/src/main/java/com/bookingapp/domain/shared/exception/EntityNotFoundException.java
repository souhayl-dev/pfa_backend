package com.bookingapp.domain.shared.exception;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " not found: " + id);
    }
}
