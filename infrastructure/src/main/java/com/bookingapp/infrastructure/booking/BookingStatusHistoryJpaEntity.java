package com.bookingapp.infrastructure.booking;

import com.bookingapp.domain.booking.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "booking_status_history")
public class BookingStatusHistoryJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "booking_id", nullable = false, length = 36)
    private UUID bookingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 20)
    private BookingStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 20)
    private BookingStatus toStatus;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "changed_by", length = 36)
    private UUID changedBy;

    @Column(length = 500)
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    protected BookingStatusHistoryJpaEntity() {
    }

    public BookingStatusHistoryJpaEntity(UUID id, UUID bookingId, BookingStatus fromStatus, BookingStatus toStatus,
                                         UUID changedBy, String reason, Instant changedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.reason = reason;
        this.changedAt = changedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public BookingStatus getFromStatus() {
        return fromStatus;
    }

    public BookingStatus getToStatus() {
        return toStatus;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public String getReason() {
        return reason;
    }

    public Instant getChangedAt() {
        return changedAt;
    }
}
