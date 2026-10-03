package com.bookingapp.application.admin;

import com.bookingapp.application.profile.UserView;
import com.bookingapp.application.provider.ProviderView;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import com.bookingapp.domain.provider.Provider;
import com.bookingapp.domain.provider.ProviderStatus;
import com.bookingapp.domain.user.User;
import com.bookingapp.domain.client.ClientRepository;
import com.bookingapp.domain.provider.ProviderRepository;
import com.bookingapp.domain.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class AdminService implements AdminUseCase {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public AdminService(ProviderRepository providerRepository, UserRepository userRepository,
                        ClientRepository clientRepository) {
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public List<ProviderView> providers(ProviderStatus status) {
        return providerRepository.findByStatus(status).stream().map(ProviderView::from).toList();
    }

    @Override
    public ProviderView approve(UUID providerId) {
        return changeStatus(providerId, Provider::approve);
    }

    @Override
    public ProviderView reject(UUID providerId) {
        return changeStatus(providerId, Provider::reject);
    }

    @Override
    public ProviderView suspend(UUID providerId) {
        return changeStatus(providerId, Provider::suspend);
    }

    private ProviderView changeStatus(UUID providerId, BiConsumer<Provider, Instant> change) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider", providerId));
        change.accept(provider, Instant.now());
        return ProviderView.from(providerRepository.save(provider));
    }

    @Override
    public List<UserView> users() {
        return userRepository.findAll().stream().map(this::view).toList();
    }

    @Override
    public UserView deactivateUser(UUID adminId, UUID userId) {
        if (adminId.equals(userId)) {
            throw new BusinessRuleException("you cannot deactivate your own account");
        }
        User user = findUser(userId);
        user.deactivate(Instant.now());
        return view(userRepository.save(user));
    }

    @Override
    public UserView activateUser(UUID userId) {
        User user = findUser(userId);
        user.activate(Instant.now());
        return view(userRepository.save(user));
    }

    private UserView view(User user) {
        return UserView.from(user, clientRepository.findByUserId(user.id()).orElse(null));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
    }
}
