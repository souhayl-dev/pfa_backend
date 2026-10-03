package com.bookingapp.application.provider;

import com.bookingapp.domain.provider.Provider;
import com.bookingapp.domain.provider.ProviderStatus;
import com.bookingapp.domain.team.MemberRole;
import com.bookingapp.domain.team.MemberStatus;
import com.bookingapp.domain.team.ProviderMember;

import java.util.UUID;

/** One entry of the "switch account" menu: a provider the user works for, and their role there. */
public record MembershipView(UUID memberId, UUID providerId, String companyName, ProviderStatus providerStatus,
                             MemberRole role, MemberStatus status) {

    public static MembershipView from(ProviderMember member, Provider provider) {
        return new MembershipView(member.id(), provider.id(), provider.companyName(), provider.status(),
                member.role(), member.status());
    }
}
