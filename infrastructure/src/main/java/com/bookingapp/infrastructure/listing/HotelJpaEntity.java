package com.bookingapp.infrastructure.listing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "hotels")
public class HotelJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "listing_id", length = 36)
    private UUID listingId;

    private Integer stars;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    protected HotelJpaEntity() {
    }

    public HotelJpaEntity(UUID listingId, Integer stars, LocalTime checkInTime, LocalTime checkOutTime) {
        this.listingId = listingId;
        this.stars = stars;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    public UUID getListingId() {
        return listingId;
    }

    public Integer getStars() {
        return stars;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }
}
