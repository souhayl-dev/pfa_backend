package com.bookingapp.api.review;

import com.bookingapp.api.shared.CurrentUser;
import com.bookingapp.application.review.ReviewUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Writing and answering reviews. Reading a listing's reviews is public, through CatalogController. */
@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewUseCase reviews;

    public ReviewController(ReviewUseCase reviews) {
        this.reviews = reviews;
    }

    /** By the client of a completed booking, once per booking. */
    @PostMapping("/bookings/{bookingId}/review")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse write(@PathVariable UUID bookingId, @Valid @RequestBody ReviewRequest request,
                                Authentication authentication) {
        return ReviewResponse.from(
                reviews.write(CurrentUser.id(authentication), bookingId, request.rating(), request.comment()));
    }

    @PutMapping("/reviews/{reviewId}")
    public ReviewResponse edit(@PathVariable UUID reviewId, @Valid @RequestBody ReviewRequest request,
                               Authentication authentication) {
        return ReviewResponse.from(
                reviews.edit(CurrentUser.id(authentication), reviewId, request.rating(), request.comment()));
    }

    /** By a member of the listing's provider. */
    @PostMapping("/reviews/{reviewId}/reply")
    public ReviewResponse reply(@PathVariable UUID reviewId, @Valid @RequestBody ReplyRequest request,
                                Authentication authentication) {
        return ReviewResponse.from(reviews.reply(CurrentUser.id(authentication), reviewId, request.text()));
    }
}
