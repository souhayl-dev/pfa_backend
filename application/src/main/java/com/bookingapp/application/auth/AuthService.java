package com.bookingapp.application.auth;

import com.bookingapp.application.profile.UserView;
import com.bookingapp.application.shared.port.EmailSender;
import com.bookingapp.application.shared.port.PasswordHasher;
import com.bookingapp.application.shared.port.TokenGenerator;
import com.bookingapp.application.shared.port.TokenIssuer;
import com.bookingapp.application.shared.port.UnitOfWork;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import com.bookingapp.domain.client.Client;
import com.bookingapp.domain.user.TokenHash;
import com.bookingapp.domain.user.User;
import com.bookingapp.domain.user.UserToken;
import com.bookingapp.domain.user.UserTokenType;
import com.bookingapp.domain.client.ClientRepository;
import com.bookingapp.domain.user.UserRepository;
import com.bookingapp.domain.user.UserTokenRepository;

import java.time.Instant;
import java.util.UUID;

public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final UserTokenRepository tokenRepository;
    private final ClientRepository clientRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;
    private final TokenGenerator tokenGenerator;
    private final EmailSender emailSender;
    private final UnitOfWork unitOfWork;

    public AuthService(UserRepository userRepository, UserTokenRepository tokenRepository,
                       ClientRepository clientRepository, PasswordHasher passwordHasher, TokenIssuer tokenIssuer,
                       TokenGenerator tokenGenerator, EmailSender emailSender, UnitOfWork unitOfWork) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.clientRepository = clientRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
        this.tokenGenerator = tokenGenerator;
        this.emailSender = emailSender;
        this.unitOfWork = unitOfWork;
    }

    /** Signing up creates only the account. The client profile is created at the first booking. */
    @Override
    public UserView register(RegisterUserCommand command) {
        String rawToken = tokenGenerator.generate();
        UserView view = unitOfWork.inTransaction(() -> {
            if (userRepository.existsByEmail(command.email())) {
                throw new BusinessRuleException("an account with this email already exists");
            }
            Instant now = Instant.now();
            User user = userRepository.save(User.register(UUID.randomUUID(), command.firstName(),
                    command.lastName(), command.email(), passwordHasher.hash(command.rawPassword()), now));
            tokenRepository.save(UserToken.issue(UUID.randomUUID(), user.id(), rawToken, UserTokenType.VERIFY_EMAIL,
                    now));
            return UserView.from(user, null);
        });
        emailSender.sendEmailVerification(view.email(), view.firstName(), rawToken);
        return view;
    }

    /** A new link replaces the earlier ones, which stop working. */
    @Override
    public void resendVerification(UUID userId) {
        String rawToken = tokenGenerator.generate();
        User user = unitOfWork.inTransaction(() -> {
            User found = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User", userId));
            if (found.verified()) {
                throw new BusinessRuleException("this email is already verified");
            }
            tokenRepository.revokeAll(userId, UserTokenType.VERIFY_EMAIL);
            tokenRepository.save(UserToken.issue(UUID.randomUUID(), userId, rawToken, UserTokenType.VERIFY_EMAIL,
                    Instant.now()));
            return found;
        });
        emailSender.sendEmailVerification(user.email(), user.firstName(), rawToken);
    }

    @Override
    public AuthResult login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .filter(User::hasPassword)
                .filter(candidate -> passwordHasher.matches(command.rawPassword(), candidate.passwordHash()))
                .orElseThrow(() -> new IllegalArgumentException("invalid email or password"));
        user.requireActive();
        Client client = clientRepository.findByUserId(user.id()).orElse(null);
        return new AuthResult(tokenIssuer.issueToken(user.id(), user.roles()), UserView.from(user, client));
    }

    @Override
    public void verifyEmail(VerifyEmailCommand command) {
        unitOfWork.inTransaction(() -> {
            UserToken token = findToken(command.token(), UserTokenType.VERIFY_EMAIL);
            Instant now = Instant.now();
            token.consume(now);
            User user = userRepository.findById(token.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User", token.userId()));
            user.markVerified(now);
            tokenRepository.save(token);
            userRepository.save(user);
        });
    }

    /** Silent when the email is unknown, so this endpoint cannot be used to find out who has an account. */
    @Override
    public void requestPasswordReset(RequestPasswordResetCommand command) {
        String rawToken = tokenGenerator.generate();
        unitOfWork.inTransaction(() -> userRepository.findByEmail(command.email())
                .filter(User::active)
                .map(user -> {
                    tokenRepository.revokeAll(user.id(), UserTokenType.RESET_PASSWORD);
                    tokenRepository.save(UserToken.issue(UUID.randomUUID(), user.id(), rawToken,
                            UserTokenType.RESET_PASSWORD, Instant.now()));
                    return user;
                }))
                .ifPresent(user -> emailSender.sendPasswordReset(user.email(), user.firstName(), rawToken));
    }

    @Override
    public void resetPassword(ResetPasswordCommand command) {
        unitOfWork.inTransaction(() -> {
            UserToken token = findToken(command.token(), UserTokenType.RESET_PASSWORD);
            Instant now = Instant.now();
            token.consume(now);
            User user = userRepository.findById(token.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User", token.userId()));
            user.changePassword(passwordHasher.hash(command.newRawPassword()), now);
            tokenRepository.save(token);
            userRepository.save(user);
            // A new password signs out every other session.
            tokenRepository.revokeAll(user.id(), UserTokenType.REFRESH);
        });
    }

    private UserToken findToken(String rawToken, UserTokenType type) {
        return tokenRepository.findByHash(TokenHash.of(rawToken), type)
                .orElseThrow(() -> new BusinessRuleException("this link has expired or was already used"));
    }
}
