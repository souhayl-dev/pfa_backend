package com.bookingapp.application.shared.port;

import java.util.function.Supplier;

/**
 * Runs a use case step inside one database transaction, so several repository writes succeed or
 * fail together. Keeps the application module free of Spring's @Transactional.
 */
public interface UnitOfWork {
    <T> T inTransaction(Supplier<T> work);

    default void inTransaction(Runnable work) {
        inTransaction(() -> {
            work.run();
            return null;
        });
    }
}
