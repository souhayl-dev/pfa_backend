package com.bookingapp.infrastructure.booking;

import com.bookingapp.domain.booking.Booking;
import com.bookingapp.domain.booking.BookingRepository;
import com.bookingapp.domain.booking.BookingStatus;
import com.bookingapp.domain.booking.BookingStatusChange;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookingRepositoryAdapter implements BookingRepository {

    private final BookingJpaRepository bookings;
    private final BookingStatusHistoryJpaRepository history;

    public BookingRepositoryAdapter(BookingJpaRepository bookings, BookingStatusHistoryJpaRepository history) {
        this.bookings = bookings;
        this.history = history;
    }

    /** The booking and its new history rows are written in one transaction. */
    @Override
    @Transactional
    public Booking save(Booking booking) {
        bookings.saveAndFlush(BookingMapper.toEntity(booking));
        List<BookingStatusChange> changes = booking.pullUnsavedChanges();
        history.saveAll(changes.stream().map(BookingMapper::toEntity).toList());
        return booking;
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        return bookings.findById(id).map(BookingMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return bookings.existsByCode(code);
    }

    @Override
    public List<Booking> findByClient(UUID clientId) {
        return bookings.findByClientIdOrderByCreatedAtDesc(clientId).stream().map(BookingMapper::toDomain).toList();
    }

    @Override
    public List<Booking> findByListing(UUID listingId, BookingStatus status) {
        return bookings.findByListing(listingId, status).stream().map(BookingMapper::toDomain).toList();
    }

    @Override
    public List<Booking> findHeld(UUID unitId, Instant startAt, Instant endAt) {
        return bookings.findHeld(unitId, startAt, endAt).stream().map(BookingMapper::toDomain).toList();
    }

    @Override
    public List<Booking> findConfirmedEndedBefore(Instant instant) {
        return bookings.findByStatusAndEndAtBefore(BookingStatus.CONFIRMED, instant).stream()
                .map(BookingMapper::toDomain)
                .toList();
    }

    @Override
    public List<BookingStatusChange> findHistory(UUID bookingId) {
        return history.findByBookingIdOrderByChangedAtAsc(bookingId).stream().map(BookingMapper::toDomain).toList();
    }
}
