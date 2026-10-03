package com.bookingapp.infrastructure.unit;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookableUnitJpaRepository extends JpaRepository<BookableUnitJpaEntity, UUID> {

    /** SELECT ... FOR UPDATE: the row stays locked until the surrounding transaction ends. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from BookableUnitJpaEntity u where u.id = :id")
    Optional<BookableUnitJpaEntity> findByIdForUpdate(@Param("id") UUID id);

    List<BookableUnitJpaEntity> findByListingIdAndDeletedAtIsNullOrderByNameAsc(UUID listingId);

    @Query("""
            select count(c) > 0 from CarJpaEntity c, BookableUnitJpaEntity u
            where c.unitId = u.id and u.deletedAt is null
              and upper(c.plateNumber) = upper(:plateNumber) and u.id <> :excludingUnitId
            """)
    boolean existsActiveCarWithPlate(@Param("plateNumber") String plateNumber,
                                     @Param("excludingUnitId") UUID excludingUnitId);
}
