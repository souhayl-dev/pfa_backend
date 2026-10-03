package com.bookingapp.api.provider;

import com.bookingapp.application.provider.MembershipView;
import com.bookingapp.domain.provider.ProviderStatus;
import com.bookingapp.domain.team.MemberRole;
import com.bookingapp.domain.team.MemberStatus;

import java.util.UUID;

/** One entry of the "switch account" menu. */
public record MembershipResponse(UUID memberId, UUID providerId, String companyName, ProviderStatus providerStatus,
                                 MemberRole role, MemberStatus status) {

    public static MembershipResponse from(MembershipView view) {
        return new MembershipResponse(view.memberId(), view.providerId(), view.companyName(),
                view.providerStatus(), view.role(), view.status());
    }
}
