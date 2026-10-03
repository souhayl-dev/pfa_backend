package com.bookingapp.application.provider;

import com.bookingapp.application.shared.ProviderAccess;
import com.bookingapp.application.shared.port.UnitOfWork;
import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import com.bookingapp.domain.provider.Provider;
import com.bookingapp.domain.team.ProviderMember;
import com.bookingapp.domain.team.ProviderMemberRepository;
import com.bookingapp.domain.provider.ProviderRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProviderService implements ProviderUseCase {

    private final ProviderRepository providerRepository;
    private final ProviderMemberRepository memberRepository;
    private final ProviderAccess access;
    private final UnitOfWork unitOfWork;

    public ProviderService(ProviderRepository providerRepository, ProviderMemberRepository memberRepository,
                           ProviderAccess access, UnitOfWork unitOfWork) {
        this.providerRepository = providerRepository;
        this.memberRepository = memberRepository;
        this.access = access;
        this.unitOfWork = unitOfWork;
    }

    @Override
    public ProviderView register(UUID userId, ProviderDetailsCommand command) {
        return unitOfWork.inTransaction(() -> {
            Instant now = Instant.now();
            Provider provider = providerRepository.save(Provider.register(UUID.randomUUID(), command.companyName(),
                    command.legalName(), command.taxId(), command.verificationDocumentUrl(), command.description(),
                    now));
            memberRepository.save(ProviderMember.owner(UUID.randomUUID(), provider.id(), userId, now));
            return ProviderView.from(provider);
        });
    }

    @Override
    public ProviderView get(UUID userId, UUID providerId) {
        access.member(userId, providerId);
        return ProviderView.from(find(providerId));
    }

    @Override
    public ProviderView update(UUID userId, UUID providerId, ProviderDetailsCommand command) {
        access.member(userId, providerId).requireCanManageProvider();
        Provider provider = find(providerId);
        provider.updateDetails(command.companyName(), command.legalName(), command.taxId(),
                command.verificationDocumentUrl(), command.description(), Instant.now());
        return ProviderView.from(providerRepository.save(provider));
    }

    @Override
    public List<MembershipView> myMemberships(UUID userId) {
        return memberRepository.findByUser(userId).stream()
                .map(member -> MembershipView.from(member, find(member.providerId())))
                .toList();
    }

    private Provider find(UUID providerId) {
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider", providerId));
    }
}
