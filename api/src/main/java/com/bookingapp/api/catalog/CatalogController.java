package com.bookingapp.api.catalog;

import com.bookingapp.api.listing.ListingResponse;
import com.bookingapp.api.listing.ListingSummaryResponse;
import com.bookingapp.api.review.ReviewResponse;
import com.bookingapp.application.catalog.CatalogUseCase;
import com.bookingapp.application.review.ReviewUseCase;
import com.bookingapp.domain.listing.ListingSearchCriteria;
import com.bookingapp.domain.listing.ListingSort;
import com.bookingapp.domain.listing.ListingType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** The public catalog: anyone can read it without signing in. */
@RestController
@RequestMapping("/api")
public class CatalogController {

    private final CatalogUseCase catalog;
    private final ReviewUseCase reviews;

    public CatalogController(CatalogUseCase catalog, ReviewUseCase reviews) {
        this.catalog = catalog;
        this.reviews = reviews;
    }

    /**
     * q is looked for in the name and the city. minPrice and maxPrice compare with the listing's
     * "from" price, and minRating leaves out listings without a review.
     */
    @GetMapping("/listings")
    public List<ListingSummaryResponse> search(@RequestParam(required = false) ListingType type,
                                               @RequestParam(required = false) String city,
                                               @RequestParam(required = false) String country,
                                               @RequestParam(required = false) String q,
                                               @RequestParam(required = false) BigDecimal minPrice,
                                               @RequestParam(required = false) BigDecimal maxPrice,
                                               @RequestParam(required = false) BigDecimal minRating,
                                               @RequestParam(defaultValue = "RECOMMENDED") ListingSort sort,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        var criteria = new ListingSearchCriteria(type, city, country, q, minPrice, maxPrice, minRating, sort,
                page, size);
        return catalog.search(criteria).stream().map(ListingSummaryResponse::from).toList();
    }

    /** The total and the count beside each filter, for the same filters as the search. */
    @GetMapping("/listings/facets")
    public ListingFacetsResponse facets(@RequestParam(required = false) ListingType type,
                                        @RequestParam(required = false) String city,
                                        @RequestParam(required = false) String country,
                                        @RequestParam(required = false) String q,
                                        @RequestParam(required = false) BigDecimal minPrice,
                                        @RequestParam(required = false) BigDecimal maxPrice,
                                        @RequestParam(required = false) BigDecimal minRating) {
        var criteria = new ListingSearchCriteria(type, city, country, q, minPrice, maxPrice, minRating, null, 0, 1);
        return ListingFacetsResponse.from(catalog.facets(criteria));
    }

    @GetMapping("/listings/{listingId}")
    public ListingResponse listing(@PathVariable UUID listingId) {
        return ListingResponse.from(catalog.listing(listingId));
    }

    @GetMapping("/listings/{listingId}/reviews")
    public List<ReviewResponse> reviews(@PathVariable UUID listingId) {
        return reviews.ofListing(listingId).stream().map(ReviewResponse::from).toList();
    }

    /**
     * The price and availability of a unit, without booking it. start and end are in the listing's
     * local time, for example 2026-11-10T14:00. A tour only needs start.
     */
    @GetMapping("/units/{unitId}/quote")
    public QuoteResponse quote(@PathVariable UUID unitId,
                               @RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                               @RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                               @RequestParam(defaultValue = "1") int guests) {
        return QuoteResponse.from(catalog.quote(unitId, start, end, guests));
    }
}
