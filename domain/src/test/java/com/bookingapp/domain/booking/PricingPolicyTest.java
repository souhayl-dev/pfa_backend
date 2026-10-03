package com.bookingapp.domain.booking;

import com.bookingapp.domain.TestData;
import com.bookingapp.domain.unit.BookableUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static com.bookingapp.domain.TestData.MARRAKECH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PricingPolicyTest {

    private static Instant at(String iso) {
        return Instant.parse(iso);
    }

    @Test
    void roomIsChargedPerNightWhateverTheNumberOfGuests() {
        BookableUnit room = TestData.room(TestData.activeHotel());

        var quote = PricingPolicy.quote(room, at("2026-10-10T13:00:00Z"), at("2026-10-12T11:00:00Z"), 2, MARRAKECH);

        assertEquals(2, quote.billedUnits());
        assertEquals(new BigDecimal("85.00"), quote.unitPrice());
        assertEquals(new BigDecimal("170.00"), quote.total());
    }

    @Test
    void stayMustCoverAtLeastOneNight() {
        BookableUnit room = TestData.room(TestData.activeHotel());

        assertThrows(IllegalArgumentException.class, () -> PricingPolicy.quote(room, at("2026-10-10T08:00:00Z"),
                at("2026-10-10T18:00:00Z"), 2, MARRAKECH));
    }

    @Test
    void carIsChargedPerStartedDay() {
        BookableUnit car = TestData.car(TestData.activeCarAgency());

        // 50 hours is 2 full days plus a started third one.
        var quote = PricingPolicy.quote(car, at("2026-11-01T09:00:00Z"), at("2026-11-03T11:00:00Z"), 2, MARRAKECH);

        assertEquals(3, quote.billedUnits());
        assertEquals(new BigDecimal("135.00"), quote.total());
    }

    @Test
    void tourIsChargedPerGuest() {
        BookableUnit tour = TestData.tour(TestData.activeTravelAgency(), 12);

        var quote = PricingPolicy.quote(tour, at("2026-10-14T23:00:00Z"), at("2026-10-17T23:00:00Z"), 3, MARRAKECH);

        assertEquals(3, quote.billedUnits());
        assertEquals(new BigDecimal("540.00"), quote.total());
    }
}
