package com.bookingapp.domain.booking;

import com.bookingapp.domain.unit.BookableUnit;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Prices a booking of one unit. The unit's type says what its base price is charged per
 * (see UnitType.PricingUnit). Nights are counted in the listing's timezone.
 */
public final class PricingPolicy {

    private PricingPolicy() {
    }

    /** @param billedUnits nights, days, trips or guests charged at unitPrice */
    public record Quote(int billedUnits, BigDecimal unitPrice, BigDecimal total) {
    }

    public static Quote quote(BookableUnit unit, Instant startAt, Instant endAt, int guestsCount, ZoneId zone) {
        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("the end must be after the start");
        }
        int billedUnits = switch (unit.type().pricingUnit()) {
            case PER_NIGHT -> {
                long nights = ChronoUnit.DAYS.between(startAt.atZone(zone).toLocalDate(),
                        endAt.atZone(zone).toLocalDate());
                if (nights < 1) {
                    throw new IllegalArgumentException("a stay must last at least one night");
                }
                yield (int) nights;
            }
            case PER_DAY -> (int) Math.max(1, Math.ceilDiv(Duration.between(startAt, endAt).toMinutes(), 24 * 60));
            case PER_TRIP -> 1;
            case PER_PERSON -> guestsCount;
        };
        BigDecimal unitPrice = unit.basePrice();
        return new Quote(billedUnits, unitPrice, unitPrice.multiply(BigDecimal.valueOf(billedUnits)));
    }
}
