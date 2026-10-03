package com.bookingapp.application.photo;

import com.bookingapp.application.shared.ProviderAccess;
import com.bookingapp.domain.photo.Photo;
import com.bookingapp.domain.photo.PhotoRepository;
import com.bookingapp.domain.shared.exception.EntityNotFoundException;

import java.time.Instant;
import java.util.UUID;

public class PhotoService implements PhotoUseCase {

    private final PhotoRepository photoRepository;
    private final ProviderAccess access;

    public PhotoService(PhotoRepository photoRepository, ProviderAccess access) {
        this.photoRepository = photoRepository;
        this.access = access;
    }

    @Override
    public PhotoView addToListing(UUID userId, UUID listingId, String url) {
        access.manageListing(userId, listingId);
        int nextOrder = photoRepository.findByListing(listingId).size();
        return PhotoView.from(photoRepository.save(
                Photo.ofListing(UUID.randomUUID(), listingId, url, nextOrder, Instant.now())));
    }

    @Override
    public PhotoView addToUnit(UUID userId, UUID unitId, String url) {
        access.manageUnit(userId, unitId);
        int nextOrder = photoRepository.findByUnit(unitId).size();
        return PhotoView.from(photoRepository.save(
                Photo.ofUnit(UUID.randomUUID(), unitId, url, nextOrder, Instant.now())));
    }

    @Override
    public void remove(UUID userId, UUID photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Photo", photoId));
        if (photo.isUnitPhoto()) {
            access.manageUnit(userId, photo.unitId());
        } else {
            access.manageListing(userId, photo.listingId());
        }
        photoRepository.delete(photoId);
    }
}
