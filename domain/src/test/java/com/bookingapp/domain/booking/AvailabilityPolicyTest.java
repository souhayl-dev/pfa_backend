package com.bookingapp.domain.booking;

import com.bookingapp.domain.TestData;
import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.shared.exception.BookingNotAvailableException;
import com.bookingapp.domain.unit.BookableUnit;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static com.bookingapp.domain.TestData.held;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AvailabilityPolicyTest {

    private final Listing hotel = TestData.activeHotel();
    private final BookableUnit room = TestData.room(hotel);

    private static Instant at(String iso) {
        return Instant.parse(iso);
    }

    @Test
    void rejectsPeriodThatHasAlreadyStarted() {
        var error = assertThrows(BookingNotAvailableException.class,
                () -> AvailabilityPolicy.checkInFuture(room, at("2026-09-30T10:00:00Z"), TestData.NOW));
        assertEquals("Patio Double Room can only be booked for a future date", error.getMessage());
    }

    @Test
    void acceptsPeriodStartingLater() {
        assertDoesNotThrow(() -> AvailabilityPolicy.checkInFuture(room, at("2026-09-30T10:00:01Z"), TestData.NOW));
    }

    @Test
    void roomRejectsOverlappingBooking() {
        List<Booking> held = List.of(held(room, "2026-10-10T13:00:00Z", "2026-10-12T11:00:00Z", 2));

        var error = assertThrows(BookingNotAvailableException.class, () -> AvailabilityPolicy.checkAvailable(room,
                at("2026-10-11T13:00:00Z"), at("2026-10-13T11:00:00Z"), 2, held));
        assertEquals("Patio Double Room is already booked for these dates", error.getMessage());
    }

    @Test
    void roomAcceptsBackToBackStays() {
        List<Booking> held = List.of(held(room, "2026-10-10T13:00:00Z", "2026-10-12T11:00:00Z", 2));

        assertDoesNotThrow(() -> AvailabilityPolicy.checkAvailable(room, at("2026-10-12T11:00:00Z"),
                at("2026-10-14T11:00:00Z"), 2, held));
    }

    @Test
    void bookingsOfOtherUnitsAreIgnored() {
        List<Booking> held = List.of(held(TestData.room(hotel), "2026-10-10T13:00:00Z", "2026-10-12T11:00:00Z", 2));

        assertDoesNotThrow(() -> AvailabilityPolicy.checkAvailable(room, at("2026-10-10T13:00:00Z"),
                at("2026-10-12T11:00:00Z"), 2, held));
    }

    @Test
    void guestsMustFitInTheUnit() {
        var error = assertThrows(BookingNotAvailableException.class, () -> AvailabilityPolicy.checkAvailable(room,
                at("2026-10-10T13:00:00Z"), at("2026-10-12T11:00:00Z"), 3, List.of()));
        assertEquals("Patio Double Room takes at most 2 guest(s)", error.getMessage());
    }

    @Test
    void inactiveUnitCannotBeBooked() {
        room.deactivate(TestData.NOW);

        assertThrows(BookingNotAvailableException.class, () -> AvailabilityPolicy.checkAvailable(room,
                at("2026-10-10T13:00:00Z"), at("2026-10-12T11:00:00Z"), 2, List.of()));
    }

    @Test
    void tourGroupsAreCountedPerStartDate() {
        BookableUnit tour = TestData.tour(TestData.activeTravelAgency(), 12);
        List<Booking> held = List.of(
                held(tour, "2026-10-14T23:00:00Z", "2026-10-17T23:00:00Z", 5),
                held(tour, "2026-10-14T23:00:00Z", "2026-10-17T23:00:00Z", 4));

        // Same start: 9 of 12 seats are taken, so 3 fit and 4 do not.
        assertDoesNotThrow(() -> AvailabilityPolicy.checkAvailable(tour, at("2026-10-14T23:00:00Z"),
                at("2026-10-17T23:00:00Z"), 3, held));
        var error = assertThrows(BookingNotAvailableException.class, () -> AvailabilityPolicy.checkAvailable(tour,
                at("2026-10-14T23:00:00Z"), at("2026-10-17T23:00:00Z"), 4, held));
        assertEquals("Atlas and Valleys has only 3 place(s) left", error.getMessage());

        // A group starting the next day overlaps in time but is a separate group with 12 free seats.
        assertDoesNotThrow(() -> AvailabilityPolicy.checkAvailable(tour, at("2026-10-15T23:00:00Z"),
                at("2026-10-18T23:00:00Z"), 12, held));
    }

    @Test
    void restaurantCountsGuestsPresentAtTheSameTime() {
        BookableUnit table = TestData.table(TestData.activeRestaurant(), 10);
        // Two parties of 8 that never sit at the same time: the peak is 8, not 16.
        List<Booking> held = List.of(
                held(table, "2026-10-15T11:00:00Z", "2026-10-15T13:00:00Z", 8),
                held(table, "2026-10-15T13:00:00Z", "2026-10-15T15:00:00Z", 8));

        assertEquals(8, AvailabilityPolicy.peakGuests(held, at("2026-10-15T00:00:00Z"), at("2026-10-16T00:00:00Z")));
        assertDoesNotThrow(() -> AvailabilityPolicy.checkAvailable(table, at("2026-10-15T12:00:00Z"),
                at("2026-10-15T14:00:00Z"), 2, held));
        assertThrows(BookingNotAvailableException.class, () -> AvailabilityPolicy.checkAvailable(table,
                at("2026-10-15T12:00:00Z"), at("2026-10-15T14:00:00Z"), 3, held));
    }
}
