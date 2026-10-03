package com.bookingapp.api.catalog;

import com.bookingapp.application.catalog.QuoteView;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * reason says why when available is false. billedUnits is the number of nights, days, trips or
 * guests charged at unitPrice. startAt and endAt are the exact UTC period that would be booked.
 */
public record QuoteResponse(boolean available, String reason, Instant startAt, Instant endAt, int billedUnits,
                            BigDecimal unitPrice, BigDecimal total, String currency) {

    public static QuoteResponse from(QuoteView view) {
        return new QuoteResponse(view.available(), view.reason(), view.startAt(), view.endAt(), view.billedUnits(),
                view.unitPrice(), view.total(), view.currency());
    }
}
