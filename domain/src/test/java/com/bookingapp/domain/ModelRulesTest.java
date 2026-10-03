package com.bookingapp.domain;

import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.listing.ListingDetails;
import com.bookingapp.domain.photo.Photo;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.exception.UnauthorizedActionException;
import com.bookingapp.domain.team.MemberRole;
import com.bookingapp.domain.team.ProviderMember;
import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.unit.TourStep;
import com.bookingapp.domain.unit.UnitDetails;
import com.bookingapp.domain.unit.UnitType;
import com.bookingapp.domain.user.UserToken;
import com.bookingapp.domain.user.UserTokenType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.bookingapp.domain.TestData.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelRulesTest {

    @Test
    void unitTypeMustMatchListingType() {
        Listing hotel = TestData.activeHotel();

        var error = assertThrows(BusinessRuleException.class, () -> BookableUnit.create(UUID.randomUUID(), hotel,
                UnitType.CAR, "Clio", null, BigDecimal.TEN, 5, null, NOW));
        assertEquals("a HOTEL listing cannot offer CAR units", error.getMessage());
    }

    @Test
    void unitDetailsMustMatchUnitType() {
        Listing hotel = TestData.activeHotel();

        assertThrows(IllegalArgumentException.class, () -> BookableUnit.create(UUID.randomUUID(), hotel,
                UnitType.ROOM, "Room", null, BigDecimal.TEN, 2,
                new UnitDetails.Transport(UnitDetails.VehicleType.CAR), NOW));
        assertThrows(NullPointerException.class, () -> BookableUnit.create(UUID.randomUUID(), hotel,
                UnitType.ROOM, "Room", null, BigDecimal.TEN, 2, null, NOW));
    }

    @Test
    void tablesTakeNoDetails() {
        BookableUnit table = TestData.table(TestData.activeRestaurant(), 40);

        assertEquals(UnitType.TABLE, table.type());
        assertEquals(null, table.details());
    }

    @Test
    void listingTypeCannotChangeThroughDetails() {
        Listing hotel = TestData.activeHotel();

        assertThrows(BusinessRuleException.class,
                () -> hotel.updateDetails(new ListingDetails.Restaurant("MOROCCAN"), NOW));
    }

    @Test
    void cachedRatingFollowsReviewChanges() {
        Listing hotel = TestData.activeHotel();

        hotel.applyReviewChange(null, 5);
        hotel.applyReviewChange(null, 4);
        assertEquals(new BigDecimal("4.50"), hotel.ratingAvg());
        assertEquals(2, hotel.reviewsCount());

        hotel.applyReviewChange(4, 2);
        assertEquals(new BigDecimal("3.50"), hotel.ratingAvg());

        hotel.applyReviewChange(5, null);
        hotel.applyReviewChange(2, null);
        assertEquals(new BigDecimal("0.00"), hotel.ratingAvg());
        assertEquals(0, hotel.reviewsCount());
    }

    @Test
    void tourStepsMustBeNumberedWithoutGapsAndFitTheDuration() {
        assertThrows(IllegalArgumentException.class, () -> new UnitDetails.Tour(3,
                List.of(new TourStep(1, 1, "Ait Benhaddou", null), new TourStep(3, 2, "Ouarzazate", null))));
        assertThrows(IllegalArgumentException.class, () -> new UnitDetails.Tour(2,
                List.of(new TourStep(1, 1, "Ait Benhaddou", null), new TourStep(2, 3, "Marrakech", null))));

        var tour = new UnitDetails.Tour(3,
                List.of(new TourStep(2, 2, "Ouarzazate", null), new TourStep(1, 1, "Ait Benhaddou", null)));
        assertEquals("Ait Benhaddou", tour.steps().get(0).city(), "steps come back in order");
    }

    @Test
    void teamRolesLimitWhatAMemberCanDo() {
        UUID provider = UUID.randomUUID();
        ProviderMember staff = ProviderMember.join(UUID.randomUUID(), provider, UUID.randomUUID(), MemberRole.STAFF,
                NOW);
        ProviderMember manager = ProviderMember.join(UUID.randomUUID(), provider, UUID.randomUUID(),
                MemberRole.MANAGER, NOW);

        assertThrows(UnauthorizedActionException.class, staff::requireCanManageListings);
        manager.requireCanManageListings();
        assertThrows(UnauthorizedActionException.class, manager::requireCanManageProvider);

        manager.requireCanManage(MemberRole.STAFF);
        assertThrows(UnauthorizedActionException.class, () -> manager.requireCanManage(MemberRole.MANAGER));
        assertThrows(UnauthorizedActionException.class, () -> manager.requireCanManage(MemberRole.OWNER));

        manager.suspend();
        assertFalse(manager.isActive());
        assertThrows(UnauthorizedActionException.class, manager::requireCanManageListings);
    }

    @Test
    void singleUseTokenCannotBeReusedOrUsedAfterExpiry() {
        UserToken token = UserToken.issue(UUID.randomUUID(), UUID.randomUUID(), "raw-token",
                UserTokenType.RESET_PASSWORD, NOW);

        assertFalse(token.isUsable(NOW.plus(UserTokenType.RESET_PASSWORD.lifetime())));
        token.consume(NOW);
        assertThrows(BusinessRuleException.class, () -> token.consume(NOW));
    }

    @Test
    void photoBelongsToAListingOrAUnit() {
        UUID id = UUID.randomUUID();

        assertTrue(Photo.ofUnit(id, UUID.randomUUID(), "/uploads/a.jpg", 0, NOW).isUnitPhoto());
        assertFalse(Photo.ofListing(id, UUID.randomUUID(), "/uploads/a.jpg", 0, NOW).isUnitPhoto());
        assertThrows(IllegalArgumentException.class,
                () -> new Photo(id, UUID.randomUUID(), UUID.randomUUID(), "/uploads/a.jpg", 0, NOW));
        assertThrows(IllegalArgumentException.class, () -> new Photo(id, null, null, "/uploads/a.jpg", 0, NOW));
    }
}
