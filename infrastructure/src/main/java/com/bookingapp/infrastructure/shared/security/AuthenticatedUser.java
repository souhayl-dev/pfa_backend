package com.bookingapp.infrastructure.shared.security;

import com.bookingapp.domain.user.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Principal stored in the Spring Security context after a JWT is validated. Every signed-in user
 * gets ROLE_USER; platform roles such as ADMIN are added on top. Provider permissions are not
 * authorities: use cases check them against the user's membership of the provider concerned.
 */
public record AuthenticatedUser(UUID userId, Set<UserRole> roles) {

    public UsernamePasswordAuthenticationToken toAuthentication() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name())));
        return new UsernamePasswordAuthenticationToken(this, null, authorities);
    }
}
