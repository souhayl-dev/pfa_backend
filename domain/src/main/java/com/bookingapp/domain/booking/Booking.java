package com.bookingapp.domain.booking;

import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.shared.Money;
import com.bookingapp.domain.shared.Require;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.unit.BookableUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * One client reserving one unit for one period. The prices are copied at booking time, so later
 * price changes never affect it. Each status change is recorded and saved to the booking's
 * history together with the booking.
 */
public class Booking {

    private final UUID id;
    private final String code;
    private final UUID clientId;
    private final UUID unitId;
    private BookingStatus status;
    private final Instant startAt;
    private final Instant endAt;
    private final int guestsCount;
    private final BigDecimal unitPrice;
    private final Money total;
    private final String specialRequests;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<BookingStatusChange> unsavedChanges = new ArrayList<>();

    public Booking(UUID id, String code, UUID clientId, UUID unitId, BookingStatus status, Instant startAt,
                   Instant endAt, int guestsCount, BigDecimal unitPrice, Money total, String specialRequests,
                   Instant createdAt, Instant updatedAt) {
        this.id = Require.notNull(id, "id");
        this.code = BookingCode.validate(code);
        this.clientId = Require.notNull(clientId, "clientId");
        this.unitId = Require.notNull(unitId, "unitId");
        this.status = Require.notNull(status, "status");
        this.startAt = Require.notNull(startAt, "startAt");
        this.endAt = Require.notNull(endAt, "endAt");
        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("the end must be after the start");
        }
        this.guestsCount = Require.positive(guestsCount, "guestsCount");
        this.unitPrice = Require.nonNegative(unitPrice, "unitPrice").setScale(2, RoundingMode.HALF_UP);
        this.total = Require.notNull(total, "total");
        this.specialRequests = Require.optional(specialRequests, "specialRequests", 1000);
        this.createdAt = Require.notNull(createdAt, "createdAt");
        this.updatedAt = Require.notNull(updatedAt, "updatedAt");
    }

    /**
     * Creates a PENDING booking. Availability must already have been checked while holding a lock on
     * the unit (see AvailabilityPolicy), and the prices come from PricingPolicy.
     */
    public static Booking place(UUID id, String code, UUID clientId, Listing listing, BookableUnit unit,
                                Instant startAt, Instant endAt, int guestsCount, BigDecimal unitPrice,
                                BigDecimal totalAmount, String specialRequests, UUID placedBy, UUID changeId,
                                Instant now) {
        if (!listing.isBookable()) {
            throw new BusinessRuleException("this listing is not open for bookings");
        }
        if (!unit.listingId().equals(listing.id())) {
            throw new BusinessRuleException("this unit does not belong to the listing");
        }
        if (guestsCount > unit.capacity()) {
            throw new BusinessRuleException(unit.name() + " takes at most " + unit.capacity() + " guest(s)");
        }
        Booking booking = new Booking(id, code, clientId, unit.id(), BookingStatus.PENDING, startAt, endAt,
                guestsCount, unitPrice, Money.of(totalAmount, listing.currency()), specialRequests, now, now);
        booking.unsavedChanges.add(new BookingStatusChange(changeId, id, null, BookingStatus.PENDING, placedBy, null,
                now));
        return booking;
    }

    public void confirm(UUID changeId, UUID changedBy, Instant now) {
        moveTo(BookingStatus.CONFIRMED, changeId, Require.notNull(changedBy, "changedBy"), null, now);
    }

    public void cancel(UUID changeId, UUID changedBy, String reason, Instant now) {
        moveTo(BookingStatus.CANCELLED, changeId, Require.notNull(changedBy, "changedBy"), reason, now);
    }

    public void markNoShow(UUID changeId, UUID changedBy, Instant now) {
        if (now.isBefore(startAt)) {
            throw new BusinessRuleException("a no-show can only be recorded once the booking has started");
        }
        moveTo(BookingStatus.NO_SHOW, changeId, Require.notNull(changedBy, "changedBy"), null, now);
    }

    /** Done by the system once the booking has ended. */
    public void complete(UUID changeId, Instant now) {
        if (now.isBefore(endAt)) {
            throw new BusinessRuleException("a booking can only be completed after it ends");
        }
        moveTo(BookingStatus.COMPLETED, changeId, null, null, now);
    }

    private void moveTo(BookingStatus next, UUID changeId, UUID changedBy, String reason, Instant now) {
        if (!status.canMoveTo(next)) {
            throw new BusinessRuleException("a " + status + " booking cannot become " + next);
        }
        unsavedChanges.add(new BookingStatusChange(changeId, id, status, next, changedBy, reason, now));
        status = next;
        updatedAt = now;
    }

    /** Hands the status changes made since loading to the repository, which saves them with the booking. */
    public List<BookingStatusChange> pullUnsavedChanges() {
        List<BookingStatusChange> changes = List.copyOf(unsavedChanges);
        unsavedChanges.clear();
        return changes;
    }

    public boolean overlaps(Instant otherStart, Instant otherEnd) {
        return startAt.isBefore(otherEnd) && otherStart.isBefore(endAt);
    }

    public boolean hasStarted(Instant now) {
        return !now.isBefore(startAt);
    }

    public boolean isCompleted() {
        return status == BookingStatus.COMPLETED;
    }

    public UUID id() {
        return id;
    }

    public String code() {
        return code;
    }

    public UUID clientId() {
        return clientId;
    }

    public UUID unitId() {
        return unitId;
    }

    public BookingStatus status() {
        return status;
    }

    public Instant startAt() {
        return startAt;
    }

    public Instant endAt() {
        return endAt;
    }

    public int guestsCount() {
        return guestsCount;
    }

    public BigDecimal unitPrice() {
        return unitPrice;
    }

    public Money total() {
        return total;
    }

    public String specialRequests() {
        return specialRequests;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
