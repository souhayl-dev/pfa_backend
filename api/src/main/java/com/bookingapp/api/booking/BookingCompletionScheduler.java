package com.bookingapp.api.booking;

import com.bookingapp.application.booking.BookingUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Moves confirmed bookings to COMPLETED once they have ended, which lets their clients review them. */
@Component
public class BookingCompletionScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingCompletionScheduler.class);

    private final BookingUseCase bookingUseCase;

    public BookingCompletionScheduler(BookingUseCase bookingUseCase) {
        this.bookingUseCase = bookingUseCase;
    }

    @Scheduled(fixedDelay = 900_000, initialDelay = 60_000)
    public void completeEndedBookings() {
        int completed = bookingUseCase.completeEnded();
        if (completed > 0) {
            log.info("Completed {} ended booking(s)", completed);
        }
    }
}
