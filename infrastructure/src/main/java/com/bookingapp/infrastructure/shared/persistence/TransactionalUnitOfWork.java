package com.bookingapp.infrastructure.shared.persistence;

import com.bookingapp.application.shared.port.UnitOfWork;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
public class TransactionalUnitOfWork implements UnitOfWork {

    private final TransactionTemplate transactionTemplate;

    public TransactionalUnitOfWork(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /** Joins a transaction already in progress, otherwise starts one; any exception rolls it back. */
    @Override
    public <T> T inTransaction(Supplier<T> work) {
        return transactionTemplate.execute(status -> work.get());
    }
}
