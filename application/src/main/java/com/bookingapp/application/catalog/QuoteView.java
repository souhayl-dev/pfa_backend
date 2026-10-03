package com.bookingapp.application.catalog;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * The price and availability of a unit for a period, without booking it. reason says why when it is
 * not available. billedUnits is the number of nights, days, trips or guests charged at unitPrice.
 */
public record QuoteView(boolean available, String reason, Instant startAt, Instant endAt, int billedUnits,
                        BigDecimal unitPrice, BigDecimal total, String currency) {
}
