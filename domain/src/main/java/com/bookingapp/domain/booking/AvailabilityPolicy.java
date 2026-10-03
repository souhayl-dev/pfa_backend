package com.bookingapp.domain.booking;

import com.bookingapp.domain.shared.exception.BookingNotAvailableException;
import com.bookingapp.domain.unit.BookableUnit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Decides whether a unit can take a new booking for a period.
 *
 * <p>The caller must hold a lock on the unit row (SELECT ... FOR UPDATE) from before it loads the
 * held bookings until the new booking is saved; otherwise two requests can both pass this check.
 *
 * <ul>
 *   <li>EXCLUSIVE (room, car, transfer, guide): no held booking may overlap the period.</li>
 *   <li>SHARED_BY_OVERLAP (restaurant table): at the busiest moment of the period, the guests
 *       already booked plus the new guests must fit in the capacity.</li>
 *   <li>SHARED_BY_START (tour): bookings starting at the same moment form one group, which must
 *       fit in the capacity. A different start date is a different group.</li>
 * </ul>
 */
public final class AvailabilityPolicy {

    private AvailabilityPolicy() {
    }

    /** A booking is for later: its period must start after now. */
    public static void checkInFuture(BookableUnit unit, Instant startAt, Instant now) {
        if (!startAt.isAfter(now)) {
            throw new BookingNotAvailableException(unit.name() + " can only be booked for a future date");
        }
    }

    /**
     * @param heldBookings bookings of this unit that still hold availability (PENDING or CONFIRMED).
     *                     Bookings of other units or outside the period are ignored.
     */
    public static void checkAvailable(BookableUnit unit, Instant startAt, Instant endAt, int guestsCount,
                                      List<Booking> heldBookings) {
        if (!unit.isBookable()) {
            throw new BookingNotAvailableException(unit.name() + " is not available for booking");
        }
        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("the end must be after the start");
        }
        if (guestsCount > unit.capacity()) {
            throw new BookingNotAvailableException(unit.name() + " takes at most " + unit.capacity() + " guest(s)");
        }
        List<Booking> overlapping = heldBookings.stream()
                .filter(booking -> booking.unitId().equals(unit.id()))
                .filter(booking -> booking.overlaps(startAt, endAt))
                .toList();

        int alreadyBooked = switch (unit.type().bookingMode()) {
            case EXCLUSIVE -> {
                if (!overlapping.isEmpty()) {
                    throw new BookingNotAvailableException(unit.name() + " is already booked for these dates");
                }
                yield 0;
            }
            case SHARED_BY_OVERLAP -> peakGuests(overlapping, startAt, endAt);
            case SHARED_BY_START -> overlapping.stream()
                    .filter(booking -> booking.startAt().equals(startAt))
                    .mapToInt(Booking::guestsCount)
                    .sum();
        };
        if (alreadyBooked + guestsCount > unit.capacity()) {
            int left = Math.max(0, unit.capacity() - alreadyBooked);
            throw new BookingNotAvailableException(unit.name() + " has only " + left + " place(s) left");
        }
    }

    /** The highest number of guests booked at any single moment inside [startAt, endAt). */
    static int peakGuests(List<Booking> bookings, Instant startAt, Instant endAt) {
        List<Map.Entry<Instant, Integer>> events = new ArrayList<>();
        for (Booking booking : bookings) {
            Instant from = booking.startAt().isBefore(startAt) ? startAt : booking.startAt();
            Instant to = booking.endAt().isAfter(endAt) ? endAt : booking.endAt();
            events.add(Map.entry(from, booking.guestsCount()));
            events.add(Map.entry(to, -booking.guestsCount()));
        }
        // At the same instant, process departures before arrivals: back-to-back bookings do not overlap.
        events.sort(Map.Entry.<Instant, Integer>comparingByKey().thenComparing(Map.Entry.comparingByValue()));
        int current = 0;
        int peak = 0;
        for (Map.Entry<Instant, Integer> event : events) {
            current += event.getValue();
            peak = Math.max(peak, current);
        }
        return peak;
    }
}
