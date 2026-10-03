package com.bookingapp.infrastructure.photo;

import com.bookingapp.domain.photo.Photo;

public final class PhotoMapper {

    private PhotoMapper() {
    }

    public static Photo toDomain(PhotoJpaEntity e) {
        return new Photo(e.getId(), e.getListingId(), e.getUnitId(), e.getUrl(), e.getSortOrder(), e.getCreatedAt());
    }

    public static PhotoJpaEntity toEntity(Photo p) {
        return new PhotoJpaEntity(p.id(), p.listingId(), p.unitId(), p.url(), p.sortOrder(), p.createdAt());
    }
}
