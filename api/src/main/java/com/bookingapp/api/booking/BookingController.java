package com.bookingapp.api.booking;

import com.bookingapp.api.shared.CurrentUser;
import com.bookingapp.application.booking.BookingUseCase;
import com.bookingapp.domain.booking.BookingStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** Bookings, for the client who booked and for the team of the listing's provider. */
@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingUseCase bookings;

    public BookingController(BookingUseCase bookings) {
        this.bookings = bookings;
    }

    /** Books one unit for one period. The booking starts PENDING until the provider confirms it. */
    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse place(@Valid @RequestBody PlaceBookingRequest request, Authentication authentication) {
        return BookingResponse.from(bookings.place(CurrentUser.id(authentication), request.toCommand()));
    }

    @GetMapping("/bookings/mine")
    public List<BookingResponse> mine(Authentication authentication) {
        return bookings.myBookings(CurrentUser.id(authentication)).stream().map(BookingResponse::from).toList();
    }

    @GetMapping("/bookings/{bookingId}")
    public BookingResponse get(@PathVariable UUID bookingId, Authentication authentication) {
        return BookingResponse.from(bookings.get(CurrentUser.id(authentication), bookingId));
    }

    @GetMapping("/bookings/{bookingId}/history")
    public List<StatusChangeResponse> history(@PathVariable UUID bookingId, Authentication authentication) {
        return bookings.history(CurrentUser.id(authentication), bookingId).stream()
                .map(StatusChangeResponse::from)
                .toList();
    }

    /** By the client until the booking starts, or by the provider's team. */
    @PostMapping("/bookings/{bookingId}/cancel")
    public BookingResponse cancel(@PathVariable UUID bookingId,
                                  @Valid @RequestBody(required = false) CancelBookingRequest request,
                                  Authentication authentication) {
        String reason = request == null ? null : request.reason();
        return BookingResponse.from(bookings.cancel(CurrentUser.id(authentication), bookingId, reason));
    }

    /** By the provider's team. */
    @PostMapping("/bookings/{bookingId}/confirm")
    public BookingResponse confirm(@PathVariable UUID bookingId, Authentication authentication) {
        return BookingResponse.from(bookings.confirm(CurrentUser.id(authentication), bookingId));
    }

    /** By the provider's team, once the booking has started. */
    @PostMapping("/bookings/{bookingId}/no-show")
    public BookingResponse noShow(@PathVariable UUID bookingId, Authentication authentication) {
        return BookingResponse.from(bookings.markNoShow(CurrentUser.id(authentication), bookingId));
    }

    /** The bookings of every unit of a listing, for the provider's team. status is optional. */
    @GetMapping("/manage/listings/{listingId}/bookings")
    public List<BookingResponse> ofListing(@PathVariable UUID listingId,
                                           @RequestParam(required = false) BookingStatus status,
                                           Authentication authentication) {
        return bookings.listingBookings(CurrentUser.id(authentication), listingId, status).stream()
                .map(BookingResponse::from)
                .toList();
    }
}
