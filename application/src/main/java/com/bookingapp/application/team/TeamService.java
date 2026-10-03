package com.bookingapp.application.team;

import com.bookingapp.application.shared.ProviderAccess;
import com.bookingapp.application.shared.port.UnitOfWork;
import com.bookingapp.domain.shared.Require;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import com.bookingapp.domain.team.MemberRole;
import com.bookingapp.domain.team.ProviderMember;
import com.bookingapp.domain.team.ProviderMemberRepository;
import com.bookingapp.domain.user.User;
import com.bookingapp.domain.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class TeamService implements TeamUseCase {

    private final ProviderMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final ProviderAccess access;
    private final UnitOfWork unitOfWork;

    public TeamService(ProviderMemberRepository memberRepository, UserRepository userRepository,
                       ProviderAccess access, UnitOfWork unitOfWork) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.access = access;
        this.unitOfWork = unitOfWork;
    }

    @Override
    public List<MemberView> members(UUID userId, UUID providerId) {
        access.member(userId, providerId);
        return memberRepository.findByProvider(providerId).stream().map(this::view).toList();
    }

    @Override
    public MemberView addMember(UUID userId, UUID providerId, String email, MemberRole role) {
        String normalizedEmail = Require.email(email, "email");
        return unitOfWork.inTransaction(() -> {
            access.member(userId, providerId).requireCanManage(role);
            User newMember = userRepository.findByEmail(normalizedEmail)
                    .filter(User::active)
                    .orElseThrow(() -> new BusinessRuleException(
                            "no account uses " + normalizedEmail + "; ask this person to sign up first"));
            if (memberRepository.findByProviderAndUser(providerId, newMember.id()).isPresent()) {
                throw new BusinessRuleException(normalizedEmail + " is already in this team");
            }
            ProviderMember member = ProviderMember.join(UUID.randomUUID(), providerId, newMember.id(), role,
                    Instant.now());
            return MemberView.from(memberRepository.save(member), newMember);
        });
    }

    @Override
    public MemberView changeRole(UUID userId, UUID memberId, MemberRole role) {
        return unitOfWork.inTransaction(() -> {
            ProviderMember target = findMember(memberId);
            ProviderMember actor = access.member(userId, target.providerId());
            actor.requireCanManage(target.role());
            actor.requireCanManage(role);
            if (target.isOwner() && role != MemberRole.OWNER) {
                requireAnotherOwner(target);
            }
            target.changeRole(role);
            return view(memberRepository.save(target));
        });
    }

    @Override
    public MemberView suspend(UUID userId, UUID memberId) {
        return unitOfWork.inTransaction(() -> {
            ProviderMember target = findMember(memberId);
            access.member(userId, target.providerId()).requireCanManage(target.role());
            if (target.isOwner()) {
                requireAnotherOwner(target);
            }
            target.suspend();
            return view(memberRepository.save(target));
        });
    }

    @Override
    public MemberView reactivate(UUID userId, UUID memberId) {
        ProviderMember target = findMember(memberId);
        access.member(userId, target.providerId()).requireCanManage(target.role());
        target.reactivate();
        return view(memberRepository.save(target));
    }

    @Override
    public void remove(UUID userId, UUID memberId) {
        unitOfWork.inTransaction(() -> {
            ProviderMember target = findMember(memberId);
            access.member(userId, target.providerId()).requireCanManage(target.role());
            if (target.isOwner()) {
                requireAnotherOwner(target);
            }
            memberRepository.delete(memberId);
        });
    }

    /** A provider must always keep at least one active owner. */
    private void requireAnotherOwner(ProviderMember owner) {
        long activeOwners = memberRepository.countActiveOwners(owner.providerId());
        long remaining = owner.isActive() ? activeOwners - 1 : activeOwners;
        if (remaining < 1) {
            throw new BusinessRuleException("a provider must keep at least one active owner");
        }
    }

    private MemberView view(ProviderMember member) {
        User user = userRepository.findById(member.userId())
                .orElseThrow(() -> new EntityNotFoundException("User", member.userId()));
        return MemberView.from(member, user);
    }

    private ProviderMember findMember(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member", memberId));
    }
}
