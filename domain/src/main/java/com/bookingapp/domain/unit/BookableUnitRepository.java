package com.bookingapp.domain.unit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookableUnitRepository {
    BookableUnit save(BookableUnit unit);

    Optional<BookableUnit> findById(UUID id);

    /**
     * Loads the unit with a row lock held until the transaction ends. Booking must go through this
     * before checking availability, so two bookings of the same unit cannot pass the check together.
     */
    Optional<BookableUnit> findByIdForUpdate(UUID id);

    /** Units that are not deleted, including inactive ones. */
    List<BookableUnit> findByListing(UUID listingId);

    /** A car plate must be unique among cars that are not deleted. */
    boolean existsActiveCarWithPlate(String plateNumber, UUID excludingUnitId);
}
