package com.bookingapp.domain.booking;

import com.bookingapp.domain.TestData;
import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.unit.BookableUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.bookingapp.domain.TestData.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingTest {

    private final Listing hotel = TestData.activeHotel();
    private final BookableUnit room = TestData.room(hotel);
    private final UUID clientUser = UUID.randomUUID();
    private final UUID staffUser = UUID.randomUUID();

    private Booking place(Listing listing, BookableUnit unit, int guests) {
        return Booking.place(UUID.randomUUID(), "BK-7Q2M4X", UUID.randomUUID(), listing, unit,
                Instant.parse("2026-10-10T13:00:00Z"), Instant.parse("2026-10-12T11:00:00Z"), guests,
                new BigDecimal("85"), new BigDecimal("170"), null, clientUser, UUID.randomUUID(), NOW);
    }

    private Booking placeTwoNights() {
        return place(hotel, room, 2);
    }

    @Test
    void placedBookingIsPendingWithPricesAndFirstHistoryEntry() {
        Booking booking = placeTwoNights();

        assertEquals(BookingStatus.PENDING, booking.status());
        assertEquals(new BigDecimal("85.00"), booking.unitPrice());
        assertEquals(new BigDecimal("170.00"), booking.total().amount());
        assertEquals("MAD", booking.total().currency());
        List<BookingStatusChange> history = booking.pullUnsavedChanges();
        assertEquals(1, history.size());
        assertNull(history.get(0).fromStatus());
        assertEquals(BookingStatus.PENDING, history.get(0).toStatus());
        assertEquals(clientUser, history.get(0).changedBy());
        assertTrue(booking.pullUnsavedChanges().isEmpty(), "changes are handed over only once");
    }

    @Test
    void everyStatusChangeIsRecorded() {
        Booking booking = placeTwoNights();
        booking.pullUnsavedChanges();

        booking.confirm(UUID.randomUUID(), staffUser, NOW);
        booking.complete(UUID.randomUUID(), Instant.parse("2026-10-12T12:00:00Z"));

        List<BookingStatusChange> history = booking.pullUnsavedChanges();
        assertEquals(List.of(BookingStatus.CONFIRMED, BookingStatus.COMPLETED),
                history.stream().map(BookingStatusChange::toStatus).toList());
        assertEquals(staffUser, history.get(0).changedBy());
        assertNull(history.get(1).changedBy(), "completion is done by the system");
    }

    @Test
    void cancellationKeepsItsReason() {
        Booking booking = placeTwoNights();
        booking.pullUnsavedChanges();

        booking.cancel(UUID.randomUUID(), clientUser, "Plans changed", NOW);

        assertEquals("Plans changed", booking.pullUnsavedChanges().get(0).reason());
    }

    @Test
    void finalStatusesCannotChange() {
        Booking booking = placeTwoNights();
        booking.cancel(UUID.randomUUID(), clientUser, "Plans changed", NOW);

        var error = assertThrows(BusinessRuleException.class,
                () -> booking.confirm(UUID.randomUUID(), staffUser, NOW));
        assertEquals("a CANCELLED booking cannot become CONFIRMED", error.getMessage());
    }

    @Test
    void pendingBookingCannotBeCompleted() {
        Booking booking = placeTwoNights();

        assertThrows(BusinessRuleException.class,
                () -> booking.complete(UUID.randomUUID(), Instant.parse("2026-10-13T00:00:00Z")));
    }

    @Test
    void bookingCannotBeCompletedBeforeItEnds() {
        Booking booking = placeTwoNights();
        booking.confirm(UUID.randomUUID(), staffUser, NOW);

        var error = assertThrows(BusinessRuleException.class,
                () -> booking.complete(UUID.randomUUID(), Instant.parse("2026-10-11T00:00:00Z")));
        assertEquals("a booking can only be completed after it ends", error.getMessage());
    }

    @Test
    void noShowNeedsTheBookingToHaveStarted() {
        Booking booking = placeTwoNights();
        booking.confirm(UUID.randomUUID(), staffUser, NOW);

        assertThrows(BusinessRuleException.class, () -> booking.markNoShow(UUID.randomUUID(), staffUser, NOW));
        booking.markNoShow(UUID.randomUUID(), staffUser, Instant.parse("2026-10-10T20:00:00Z"));
        assertEquals(BookingStatus.NO_SHOW, booking.status());
    }

    @Test
    void unitMustBelongToTheListing() {
        BookableUnit foreignRoom = TestData.room(TestData.activeHotel());

        assertThrows(BusinessRuleException.class, () -> place(hotel, foreignRoom, 2));
    }

    @Test
    void inactiveListingCannotBeBooked() {
        hotel.deactivate(NOW);

        var error = assertThrows(BusinessRuleException.class, this::placeTwoNights);
        assertEquals("this listing is not open for bookings", error.getMessage());
    }

    @Test
    void guestsCannotExceedTheUnitCapacity() {
        var error = assertThrows(BusinessRuleException.class, () -> place(hotel, room, 3));
        assertEquals("Patio Double Room takes at most 2 guest(s)", error.getMessage());
    }

    @Test
    void generatedCodesMatchTheFormat() {
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 100; i++) {
            BookingCode.validate(BookingCode.generate(random));
        }
    }
}
