package com.bookingapp.infrastructure.unit;

import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.unit.BookableUnitRepository;
import com.bookingapp.domain.unit.UnitDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A unit is stored as its shared row in bookable_units plus, for rooms, cars, transports and tours,
 * a row in the matching details table. Both are written in one transaction.
 */
@Repository
public class BookableUnitRepositoryAdapter implements BookableUnitRepository {

    private final BookableUnitJpaRepository units;
    private final RoomJpaRepository rooms;
    private final CarJpaRepository cars;
    private final TransportJpaRepository transports;
    private final TourJpaRepository tours;

    public BookableUnitRepositoryAdapter(BookableUnitJpaRepository units, RoomJpaRepository rooms,
                                         CarJpaRepository cars, TransportJpaRepository transports,
                                         TourJpaRepository tours) {
        this.units = units;
        this.rooms = rooms;
        this.cars = cars;
        this.transports = transports;
        this.tours = tours;
    }

    @Override
    @Transactional
    public BookableUnit save(BookableUnit unit) {
        // The shared row must exist first: the details row references it.
        units.saveAndFlush(BookableUnitMapper.toEntity(unit));
        if (unit.details() != null) {
            saveDetails(unit.id(), unit.details());
        }
        return unit;
    }

    private void saveDetails(UUID id, UnitDetails details) {
        switch (details) {
            case UnitDetails.Room r -> rooms.save(BookableUnitMapper.toEntity(id, r));
            case UnitDetails.Car c -> cars.save(BookableUnitMapper.toEntity(id, c));
            case UnitDetails.Transport t -> transports.save(BookableUnitMapper.toEntity(id, t));
            case UnitDetails.Tour t -> tours.save(BookableUnitMapper.toEntity(id, t));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookableUnit> findById(UUID id) {
        return units.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional
    public Optional<BookableUnit> findByIdForUpdate(UUID id) {
        return units.findByIdForUpdate(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookableUnit> findByListing(UUID listingId) {
        return units.findByListingIdAndDeletedAtIsNullOrderByNameAsc(listingId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsActiveCarWithPlate(String plateNumber, UUID excludingUnitId) {
        // A random id never matches, so "exclude nothing" needs no separate query.
        UUID excluded = excludingUnitId != null ? excludingUnitId : UUID.randomUUID();
        return units.existsActiveCarWithPlate(plateNumber.strip(), excluded);
    }

    private BookableUnit toDomain(BookableUnitJpaEntity entity) {
        return BookableUnitMapper.toDomain(entity, loadDetails(entity));
    }

    private UnitDetails loadDetails(BookableUnitJpaEntity entity) {
        UUID id = entity.getId();
        if (!entity.getType().hasDetails()) {
            return null;
        }
        Optional<UnitDetails> details = switch (entity.getType()) {
            case ROOM -> rooms.findById(id).map(BookableUnitMapper::toDomain);
            case CAR -> cars.findById(id).map(BookableUnitMapper::toDomain);
            case TRANSPORT -> transports.findById(id).map(BookableUnitMapper::toDomain);
            case TOUR -> tours.findById(id).map(BookableUnitMapper::toDomain);
            case TABLE, GUIDE_SERVICE -> Optional.empty();
        };
        return details.orElseThrow(() -> new EntityNotFoundException(entity.getType() + " details", id));
    }
}
