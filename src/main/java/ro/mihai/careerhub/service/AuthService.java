package ro.mihai.careerhub.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import ro.mihai.careerhub.dto.request.LoginRequest;
import ro.mihai.careerhub.dto.response.LoginResponse;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder) {

        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        Instant issuedAt = Instant.now();

        Instant expiresAt =
                issuedAt.plus(
                        jwtExpiration,
                        ChronoUnit.MILLIS
                );

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .subject(authentication.getName())
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .claim(
                                "roles",
                                authentication
                                        .getAuthorities()
                                        .stream()
                                        .map(
                                                authority ->
                                                        authority.getAuthority()
                                        )
                                        .toList()
                        )
                        .build();

        String token =
                jwtEncoder
                        .encode(
                                JwtEncoderParameters.from(claims)
                        )
                        .getTokenValue();

        return new LoginResponse(token);
    }
}