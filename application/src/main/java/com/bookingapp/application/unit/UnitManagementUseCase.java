package com.bookingapp.application.unit;

import java.util.UUID;

/** The bookable units of a listing. Needs an owner or manager of the listing's provider. */
public interface UnitManagementUseCase {
    UnitView create(UUID userId, UUID listingId, UnitCommand command);

    UnitView update(UUID userId, UUID unitId, UnitCommand command);

    UnitView activate(UUID userId, UUID unitId);

    UnitView deactivate(UUID userId, UUID unitId);

    void delete(UUID userId, UUID unitId);
}
