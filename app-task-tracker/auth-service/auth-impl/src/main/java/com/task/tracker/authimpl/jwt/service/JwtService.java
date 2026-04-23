package com.task.tracker.authimpl.jwt.service;


import com.task.tracker.authapi.dto.AccountResponse;
import com.task.tracker.authapi.dto.TokenCouple;
import com.task.tracker.authimpl.entity.Account;
import com.task.tracker.authimpl.exception.AuthenticationHeaderException;
import com.task.tracker.authimpl.exception.InvalidTokenException;
import com.task.tracker.authimpl.jwt.properties.JwtTokenProperties;
import com.task.tracker.authimpl.jwt.provider.JwtAccessTokenProvider;
import com.task.tracker.authimpl.jwt.provider.JwtRefreshTokenProvider;
import com.task.tracker.authapi.status.Role;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtAccessTokenProvider jwtAccessTokenProvider;
    private final JwtRefreshTokenProvider jwtRefreshTokenProvider;
    private static final String BEARER = "Bearer";
    private final JwtTokenProperties jwtTokenProperties;

    public TokenCouple generateTokenCouple(Account account) {
        String accessToken =
                jwtAccessTokenProvider.generateAccessToken(account);
        String refreshToken =
                jwtRefreshTokenProvider.generateRefreshToken(account.getUsername());

        return new TokenCouple(
                BEARER.concat(" ").concat(accessToken),
                refreshToken,
                jwtTokenProperties.getExpiresAt()
        );
    }

    public TokenCouple refreshAccessToken(Account account) {

        String accessToken =
                jwtAccessTokenProvider.generateAccessToken(account);

        String refreshToken =
                jwtRefreshTokenProvider.generateRefreshToken(account.getUsername());

        return new TokenCouple(
                BEARER.concat(" ").concat(accessToken),
                refreshToken,
                jwtTokenProperties.getExpiresAt()
        );
    }

    public AccountResponse validateToken(String token) {
        Claims claims = parseAccessToken(token);

        if (!isValidAccessToken(token, claims.getSubject())) {
            throw new InvalidTokenException("Invalid token");
        }

        List<String> roles = claims.get("roles", List.class);

        Set<Role> roleList = roles.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());


        return new AccountResponse(
                claims.get("id", UUID.class),
                claims.getSubject(),
                roleList
        );
    }

    private boolean isValidAccessToken(String accessToken, String subject)
            throws AuthenticationHeaderException {
        return jwtAccessTokenProvider.validateAccessToken(accessToken, subject);
    }

    private Claims parseAccessToken(String accessToken)
            throws AuthenticationHeaderException {
        return jwtAccessTokenProvider.parseAccessToken(accessToken);
    }

    private Claims parseRefreshToken(String accessToken)
            throws AuthenticationHeaderException {
        return jwtRefreshTokenProvider.parseRefreshToken(accessToken);
    }


    public void invalidateRefreshToken(String requestRefreshToken) {
        jwtRefreshTokenProvider.invalidate(requestRefreshToken);
    }
}