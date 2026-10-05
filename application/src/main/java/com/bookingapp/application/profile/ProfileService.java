package com.bookingapp.application.profile;

import com.bookingapp.application.shared.port.PasswordHasher;
import com.bookingapp.application.shared.port.UnitOfWork;
import com.bookingapp.domain.client.Client;
import com.bookingapp.domain.client.ClientRepository;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import com.bookingapp.domain.shared.exception.UnauthorizedActionException;
import com.bookingapp.domain.user.User;
import com.bookingapp.domain.user.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

public class ProfileService implements ProfileUseCase {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordHasher passwordHasher;
    private final UnitOfWork unitOfWork;

    public ProfileService(UserRepository userRepository, ClientRepository clientRepository,
                          PasswordHasher passwordHasher, UnitOfWork unitOfWork) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordHasher = passwordHasher;
        this.unitOfWork = unitOfWork;
    }

    @Override
    public UserView getProfile(UUID userId) {
        return view(findUser(userId));
    }

    @Override
    public UserView updateProfile(UpdateProfileCommand command) {
        User user = findUser(command.userId());
        String username = command.username() == null ? null : command.username().strip().toLowerCase();
        if (username != null && !username.isEmpty() && !Objects.equals(username, user.username())
                && userRepository.existsByUsername(username)) {
            throw new BusinessRuleException("this username is already taken");
        }
        Instant now = Instant.now();
        user.updateProfile(command.firstName(), command.lastName(), command.username(), command.phone(),
                command.gender(), command.notificationsEnabled(), now);
        user.changeProfileImage(command.profileImage(), now);
        return view(userRepository.save(user));
    }

    @Override
    public UserView updateClientDetails(UpdateClientDetailsCommand command) {
        return unitOfWork.inTransaction(() -> {
            User user = findUser(command.userId());
            Instant now = Instant.now();
            Client client = clientRepository.findByUserId(user.id())
                    .orElseGet(() -> Client.create(UUID.randomUUID(), user.id(), now));
            client.updateDetails(command.nationality(), command.birthDate(), LocalDate.now(ZoneOffset.UTC), now);
            return UserView.from(user, clientRepository.save(client));
        });
    }

    @Override
    public void changePassword(ChangePasswordCommand command) {
        User user = findUser(command.userId());
        if (!user.hasPassword() || !passwordHasher.matches(command.currentRawPassword(), user.passwordHash())) {
            throw new UnauthorizedActionException("current password is incorrect");
        }
        user.changePassword(passwordHasher.hash(command.newRawPassword()), Instant.now());
        userRepository.save(user);
    }

    private UserView view(User user) {
        return UserView.from(user, clientRepository.findByUserId(user.id()).orElse(null));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
    }
}
