package com.bookingapp.infrastructure.listing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "restaurants")
public class RestaurantJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "listing_id", length = 36)
    private UUID listingId;

    @Column(name = "cuisine_type", length = 50)
    private String cuisineType;

    protected RestaurantJpaEntity() {
    }

    public RestaurantJpaEntity(UUID listingId, String cuisineType) {
        this.listingId = listingId;
        this.cuisineType = cuisineType;
    }

    public UUID getListingId() {
        return listingId;
    }

    public String getCuisineType() {
        return cuisineType;
    }
}
