package com.bookingapp.application.booking;

import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.listing.ListingDetails;
import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.unit.UnitDetails;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Turns what a client picked, in the listing's local time, into the exact UTC period that is booked.
 * Rooms use the hotel's check-in and check-out times on the chosen dates; a tour starts at the
 * beginning of the chosen day and lasts its number of days; tables and transfers get a default
 * length when no end is given.
 */
public final class BookingPeriods {

    private static final LocalTime DEFAULT_CHECK_IN = LocalTime.of(14, 0);
    private static final LocalTime DEFAULT_CHECK_OUT = LocalTime.of(12, 0);
    private static final Duration DEFAULT_TABLE_LENGTH = Duration.ofHours(2);
    private static final Duration DEFAULT_TRANSFER_LENGTH = Duration.ofHours(1);

    private BookingPeriods() {
    }

    public record Period(Instant startAt, Instant endAt) {
    }

    public static Period resolve(Listing listing, BookableUnit unit, LocalDateTime start, LocalDateTime end) {
        ZoneId zone = listing.location().timezone();
        return switch (unit.type()) {
            case ROOM -> {
                requireGiven(start, end);
                LocalTime checkIn = DEFAULT_CHECK_IN;
                LocalTime checkOut = DEFAULT_CHECK_OUT;
                if (listing.details() instanceof ListingDetails.Hotel hotel) {
                    checkIn = hotel.checkInTime() != null ? hotel.checkInTime() : checkIn;
                    checkOut = hotel.checkOutTime() != null ? hotel.checkOutTime() : checkOut;
                }
                yield new Period(start.toLocalDate().atTime(checkIn).atZone(zone).toInstant(),
                        end.toLocalDate().atTime(checkOut).atZone(zone).toInstant());
            }
            case TOUR -> {
                if (start == null) {
                    throw new IllegalArgumentException("a start date is required");
                }
                UnitDetails.Tour tour = (UnitDetails.Tour) unit.details();
                // Every booking of the same start date gets the same startAt, which is what groups them.
                yield new Period(start.toLocalDate().atStartOfDay(zone).toInstant(),
                        start.toLocalDate().plusDays(tour.durationDays()).atStartOfDay(zone).toInstant());
            }
            case TABLE -> withDefaultLength(start, end, DEFAULT_TABLE_LENGTH, zone);
            case TRANSPORT -> withDefaultLength(start, end, DEFAULT_TRANSFER_LENGTH, zone);
            case CAR, GUIDE_SERVICE -> {
                requireGiven(start, end);
                yield new Period(start.atZone(zone).toInstant(), end.atZone(zone).toInstant());
            }
        };
    }

    private static Period withDefaultLength(LocalDateTime start, LocalDateTime end, Duration length, ZoneId zone) {
        if (start == null) {
            throw new IllegalArgumentException("a start time is required");
        }
        LocalDateTime actualEnd = end != null ? end : start.plus(length);
        return new Period(start.atZone(zone).toInstant(), actualEnd.atZone(zone).toInstant());
    }

    private static void requireGiven(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("a start and an end are required");
        }
    }
}
