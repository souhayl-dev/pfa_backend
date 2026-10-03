package com.bookingapp.infrastructure.shared.security;

import com.bookingapp.domain.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenIssuer jwtTokenIssuer;

    public JwtAuthenticationFilter(JwtTokenIssuer jwtTokenIssuer) {
        this.jwtTokenIssuer = jwtTokenIssuer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(jwtTokenIssuer.signingKey())
                        .build()
                        .parseSignedClaims(header.substring(7))
                        .getPayload();
                UUID userId = UUID.fromString(claims.getSubject());
                SecurityContextHolder.getContext()
                        .setAuthentication(new AuthenticatedUser(userId, roles(claims)).toAuthentication());
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    private static Set<UserRole> roles(Claims claims) {
        Set<UserRole> roles = EnumSet.noneOf(UserRole.class);
        List<?> names = claims.get(JwtTokenIssuer.ROLES_CLAIM, List.class);
        if (names != null) {
            names.forEach(name -> roles.add(UserRole.valueOf(String.valueOf(name))));
        }
        return roles;
    }
}
