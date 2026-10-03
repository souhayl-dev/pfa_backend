package com.bookingapp.infrastructure.review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class ReviewJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "booking_id", nullable = false, length = 36)
    private UUID bookingId;

    @Column(nullable = false)
    private int rating;

    @Column(length = 2000)
    private String comment;

    @Column(length = 2000)
    private String reply;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "replied_by", length = 36)
    private UUID repliedBy;

    @Column(name = "replied_at")
    private Instant repliedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ReviewJpaEntity() {
    }

    public ReviewJpaEntity(UUID id, UUID bookingId, int rating, String comment, String reply, UUID repliedBy,
                           Instant repliedAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
        this.reply = reply;
        this.repliedBy = repliedBy;
        this.repliedAt = repliedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getReply() {
        return reply;
    }

    public UUID getRepliedBy() {
        return repliedBy;
    }

    public Instant getRepliedAt() {
        return repliedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
