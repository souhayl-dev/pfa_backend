package com.bookingapp.infrastructure.review;

import com.bookingapp.domain.review.Review;
import com.bookingapp.domain.review.ReviewRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReviewRepositoryAdapter implements ReviewRepository {

    private final ReviewJpaRepository jpaRepository;

    public ReviewRepositoryAdapter(ReviewJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Review save(Review review) {
        return ReviewMapper.toDomain(jpaRepository.save(ReviewMapper.toEntity(review)));
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return jpaRepository.findById(id).map(ReviewMapper::toDomain);
    }

    @Override
    public Optional<Review> findByBooking(UUID bookingId) {
        return jpaRepository.findByBookingId(bookingId).map(ReviewMapper::toDomain);
    }

    @Override
    public List<Review> findByListing(UUID listingId) {
        return jpaRepository.findByListing(listingId).stream().map(ReviewMapper::toDomain).toList();
    }
}
