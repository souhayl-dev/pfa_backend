package com.bookingapp.api.listing;

import com.bookingapp.api.shared.CurrentUser;
import com.bookingapp.application.listing.ListingManagementUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * The provider's side of listings. The whole team can read; owners and managers can change.
 * The public reads listings through CatalogController.
 */
@RestController
@RequestMapping("/api")
public class ListingController {

    private final ListingManagementUseCase listings;

    public ListingController(ListingManagementUseCase listings) {
        this.listings = listings;
    }

    @PostMapping("/providers/{providerId}/listings")
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse create(@PathVariable UUID providerId, @Valid @RequestBody ListingRequest request,
                                  Authentication authentication) {
        return ListingResponse.from(listings.create(CurrentUser.id(authentication), providerId, request.toCommand()));
    }

    @GetMapping("/providers/{providerId}/listings")
    public List<ListingSummaryResponse> ofProvider(@PathVariable UUID providerId, Authentication authentication) {
        return listings.listForProvider(CurrentUser.id(authentication), providerId).stream()
                .map(ListingSummaryResponse::from)
                .toList();
    }

    /** Includes drafts and inactive units, which the public cannot see. */
    @GetMapping("/manage/listings/{listingId}")
    public ListingResponse get(@PathVariable UUID listingId, Authentication authentication) {
        return ListingResponse.from(listings.get(CurrentUser.id(authentication), listingId));
    }

    @PutMapping("/manage/listings/{listingId}")
    public ListingResponse update(@PathVariable UUID listingId, @Valid @RequestBody ListingRequest request,
                                  Authentication authentication) {
        return ListingResponse.from(listings.update(CurrentUser.id(authentication), listingId, request.toCommand()));
    }

    /** Makes the listing public. Only works once the provider is approved. */
    @PostMapping("/manage/listings/{listingId}/activate")
    public ListingResponse activate(@PathVariable UUID listingId, Authentication authentication) {
        return ListingResponse.from(listings.activate(CurrentUser.id(authentication), listingId));
    }

    @PostMapping("/manage/listings/{listingId}/deactivate")
    public ListingResponse deactivate(@PathVariable UUID listingId, Authentication authentication) {
        return ListingResponse.from(listings.deactivate(CurrentUser.id(authentication), listingId));
    }

    @DeleteMapping("/manage/listings/{listingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID listingId, Authentication authentication) {
        listings.delete(CurrentUser.id(authentication), listingId);
    }
}
