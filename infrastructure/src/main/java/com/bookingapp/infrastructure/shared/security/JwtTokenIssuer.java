package com.bookingapp.infrastructure.shared.security;

import com.bookingapp.application.shared.port.TokenIssuer;
import com.bookingapp.domain.user.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtTokenIssuer implements TokenIssuer {

    static final String ROLES_CLAIM = "roles";

    private final SecretKey signingKey;
    private final long expirationMinutes;

    public JwtTokenIssuer(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = properties.getExpirationMinutes();
    }

    @Override
    public String issueToken(UUID userId, Set<UserRole> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim(ROLES_CLAIM, roles.stream().map(Enum::name).sorted().toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    public SecretKey signingKey() {
        return signingKey;
    }
}
