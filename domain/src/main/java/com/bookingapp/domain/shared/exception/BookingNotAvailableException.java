package com.bookingapp.domain.shared.exception;

public class BookingNotAvailableException extends DomainException {
    public BookingNotAvailableException(String message) {
        super(message);
    }
}
