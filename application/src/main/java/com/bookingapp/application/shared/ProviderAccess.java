package com.bookingapp.application.shared;

import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import com.bookingapp.domain.shared.exception.UnauthorizedActionException;
import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.team.ProviderMember;
import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.listing.ListingRepository;
import com.bookingapp.domain.team.ProviderMemberRepository;
import com.bookingapp.domain.unit.BookableUnitRepository;

import java.util.UUID;

/**
 * The one place that answers "may this user act for this provider, listing or unit?". Provider
 * permissions are not roles in the token: they come from the user's membership, read on every call,
 * so removing someone from a team takes effect immediately.
 */
public class ProviderAccess {

    public record ListingAccess(Listing listing, ProviderMember member) {
    }

    public record UnitAccess(BookableUnit unit, Listing listing, ProviderMember member) {
    }

    private final ProviderMemberRepository memberRepository;
    private final ListingRepository listingRepository;
    private final BookableUnitRepository unitRepository;

    public ProviderAccess(ProviderMemberRepository memberRepository, ListingRepository listingRepository,
                          BookableUnitRepository unitRepository) {
        this.memberRepository = memberRepository;
        this.listingRepository = listingRepository;
        this.unitRepository = unitRepository;
    }

    /** An active member of the provider, whatever their role. */
    public ProviderMember member(UUID userId, UUID providerId) {
        return memberRepository.findByProviderAndUser(providerId, userId)
                .filter(ProviderMember::isActive)
                .orElseThrow(() -> new UnauthorizedActionException("you are not a member of this provider"));
    }

    /** Any active member of the listing's provider: they can see it and handle its bookings and reviews. */
    public ListingAccess listing(UUID userId, UUID listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing", listingId));
        return new ListingAccess(listing, member(userId, listing.providerId()));
    }

    /** An owner or manager, who may also change the listing and its units. */
    public ListingAccess manageListing(UUID userId, UUID listingId) {
        ListingAccess access = listing(userId, listingId);
        access.member().requireCanManageListings();
        return access;
    }

    public UnitAccess manageUnit(UUID userId, UUID unitId) {
        BookableUnit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unit", unitId));
        ListingAccess access = manageListing(userId, unit.listingId());
        return new UnitAccess(unit, access.listing(), access.member());
    }

    public boolean isMemberOf(UUID userId, UUID providerId) {
        return memberRepository.findByProviderAndUser(providerId, userId).isPresent();
    }
}
