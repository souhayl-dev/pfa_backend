package com.bookingapp.infrastructure.photo;

import com.bookingapp.domain.photo.Photo;
import com.bookingapp.domain.photo.PhotoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PhotoRepositoryAdapter implements PhotoRepository {

    private final PhotoJpaRepository jpaRepository;

    public PhotoRepositoryAdapter(PhotoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Photo save(Photo photo) {
        return PhotoMapper.toDomain(jpaRepository.save(PhotoMapper.toEntity(photo)));
    }

    @Override
    public Optional<Photo> findById(UUID id) {
        return jpaRepository.findById(id).map(PhotoMapper::toDomain);
    }

    @Override
    public List<Photo> findByListing(UUID listingId) {
        return jpaRepository.findByListingIdOrderBySortOrderAsc(listingId).stream()
                .map(PhotoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Photo> findByUnit(UUID unitId) {
        return jpaRepository.findByUnitIdOrderBySortOrderAsc(unitId).stream().map(PhotoMapper::toDomain).toList();
    }

    @Override
    public void delete(UUID photoId) {
        jpaRepository.deleteById(photoId);
    }
}
