package com.bookingapp.domain.unit;

import com.bookingapp.domain.listing.ListingType;

/**
 * What a client books. The type decides which kind of listing offers it, how availability is
 * checked and what the base price is charged per. Mirrors trg_bookable_units_listing_type.
 */
public enum UnitType {
    ROOM(ListingType.HOTEL, BookingMode.EXCLUSIVE, PricingUnit.PER_NIGHT),
    TABLE(ListingType.RESTAURANT, BookingMode.SHARED_BY_OVERLAP, PricingUnit.PER_PERSON),
    GUIDE_SERVICE(ListingType.GUIDE, BookingMode.EXCLUSIVE, PricingUnit.PER_DAY),
    TRANSPORT(ListingType.TRAVEL_AGENCY, BookingMode.EXCLUSIVE, PricingUnit.PER_TRIP),
    TOUR(ListingType.TRAVEL_AGENCY, BookingMode.SHARED_BY_START, PricingUnit.PER_PERSON),
    CAR(ListingType.CAR_RENTAL_AGENCY, BookingMode.EXCLUSIVE, PricingUnit.PER_DAY);

    /** What the base price is charged per. */
    public enum PricingUnit {
        /** Each night between the check-in and check-out dates. */
        PER_NIGHT,
        /** Each started 24 hours. */
        PER_DAY,
        /** Once per booking, whatever its length. */
        PER_TRIP,
        /** Once per guest. */
        PER_PERSON
    }

    private final ListingType listingType;
    private final BookingMode bookingMode;
    private final PricingUnit pricingUnit;

    UnitType(ListingType listingType, BookingMode bookingMode, PricingUnit pricingUnit) {
        this.listingType = listingType;
        this.bookingMode = bookingMode;
        this.pricingUnit = pricingUnit;
    }

    public ListingType listingType() {
        return listingType;
    }

    public BookingMode bookingMode() {
        return bookingMode;
    }

    public PricingUnit pricingUnit() {
        return pricingUnit;
    }

    /** Tables and guide services need no extra fields, so they have no details table. */
    public boolean hasDetails() {
        return this != TABLE && this != GUIDE_SERVICE;
    }
}
