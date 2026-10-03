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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class BookingJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String code;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "client_id", nullable = false, length = 36)
    private UUID clientId;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "unit_id", nullable = false, length = 36)
    private UUID unitId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(name = "guests_count", nullable = false)
    private int guestsCount;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "special_requests", length = 1000)
    private String specialRequests;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BookingJpaEntity() {
    }

    public BookingJpaEntity(UUID id, String code, UUID clientId, UUID unitId, BookingStatus status, Instant startAt,
                            Instant endAt, int guestsCount, BigDecimal unitPrice, BigDecimal totalAmount,
                            String currency, String specialRequests, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.clientId = clientId;
        this.unitId = unitId;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.guestsCount = guestsCount;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.specialRequests = specialRequests;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public UUID getClientId() {
        return clientId;
    }

    public UUID getUnitId() {
        return unitId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public int getGuestsCount() {
        return guestsCount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
