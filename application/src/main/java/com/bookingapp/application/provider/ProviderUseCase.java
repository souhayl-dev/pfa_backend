package com.bookingapp.application.provider;

import java.util.List;
import java.util.UUID;

public interface ProviderUseCase {
    /** Any signed-in user can start a business: it is created PENDING, with them as its owner. */
    ProviderView register(UUID userId, ProviderDetailsCommand command);

    ProviderView get(UUID userId, UUID providerId);

    ProviderView update(UUID userId, UUID providerId, ProviderDetailsCommand command);

    /** The providers this user works for, for the "switch account" menu. */
    List<MembershipView> myMemberships(UUID userId);
}
