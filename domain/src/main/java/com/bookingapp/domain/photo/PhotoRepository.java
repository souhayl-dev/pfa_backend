package com.bookingapp.domain.photo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhotoRepository {
    Photo save(Photo photo);

    Optional<Photo> findById(UUID id);

    /** The listing's own photos, in display order. Unit photos are not included. */
    List<Photo> findByListing(UUID listingId);

    /** In display order. */
    List<Photo> findByUnit(UUID unitId);

    void delete(UUID photoId);
}
