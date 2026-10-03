package com.bookingapp.application.unit;

import com.bookingapp.application.shared.ProviderAccess;
import com.bookingapp.application.shared.ProviderAccess.UnitAccess;
import com.bookingapp.application.shared.port.UnitOfWork;
import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.photo.PhotoRepository;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.unit.BookableUnitRepository;
import com.bookingapp.domain.unit.UnitDetails;

import java.time.Instant;
import java.util.UUID;

public class UnitManagementService implements UnitManagementUseCase {

    private final BookableUnitRepository unitRepository;
    private final PhotoRepository photoRepository;
    private final ProviderAccess access;
    private final UnitOfWork unitOfWork;

    public UnitManagementService(BookableUnitRepository unitRepository, PhotoRepository photoRepository,
                                 ProviderAccess access, UnitOfWork unitOfWork) {
        this.unitRepository = unitRepository;
        this.photoRepository = photoRepository;
        this.access = access;
        this.unitOfWork = unitOfWork;
    }

    @Override
    public UnitView create(UUID userId, UUID listingId, UnitCommand command) {
        return unitOfWork.inTransaction(() -> {
            Listing listing = access.manageListing(userId, listingId).listing();
            UUID unitId = UUID.randomUUID();
            requireFreePlate(unitId, command.details());
            BookableUnit unit = BookableUnit.create(unitId, listing, command.type(), command.name(),
                    command.description(), command.basePrice(), command.capacity(), command.details(),
                    Instant.now());
            return view(unitRepository.save(unit), listing);
        });
    }

    @Override
    public UnitView update(UUID userId, UUID unitId, UnitCommand command) {
        return unitOfWork.inTransaction(() -> {
            UnitAccess unitAccess = access.manageUnit(userId, unitId);
            BookableUnit unit = unitAccess.unit();
            if (command.type() != null && command.type() != unit.type()) {
                throw new BusinessRuleException("the type of a unit cannot change");
            }
            requireFreePlate(unitId, command.details());
            unit.update(command.name(), command.description(), command.basePrice(), command.capacity(),
                    command.details(), Instant.now());
            return view(unitRepository.save(unit), unitAccess.listing());
        });
    }

    /** A plate number is unique among cars that are not deleted. */
    private void requireFreePlate(UUID unitId, UnitDetails details) {
        if (details instanceof UnitDetails.Car car
                && unitRepository.existsActiveCarWithPlate(car.plateNumber(), unitId)) {
            throw new BusinessRuleException("another car already uses the plate " + car.plateNumber());
        }
    }

    @Override
    public UnitView activate(UUID userId, UUID unitId) {
        UnitAccess unitAccess = access.manageUnit(userId, unitId);
        unitAccess.unit().activate(Instant.now());
        return view(unitRepository.save(unitAccess.unit()), unitAccess.listing());
    }

    @Override
    public UnitView deactivate(UUID userId, UUID unitId) {
        UnitAccess unitAccess = access.manageUnit(userId, unitId);
        unitAccess.unit().deactivate(Instant.now());
        return view(unitRepository.save(unitAccess.unit()), unitAccess.listing());
    }

    @Override
    public void delete(UUID userId, UUID unitId) {
        BookableUnit unit = access.manageUnit(userId, unitId).unit();
        unit.softDelete(Instant.now());
        unitRepository.save(unit);
    }

    private UnitView view(BookableUnit unit, Listing listing) {
        return UnitView.from(unit, listing.currency(), photoRepository.findByUnit(unit.id()));
    }
}
