package com.task.tracker.authimpl.jwt.provider;

import com.task.tracker.authimpl.entity.RefreshToken;
import com.task.tracker.authimpl.jwt.properties.JwtTokenProperties;
import com.task.tracker.authimpl.repository.RefreshTokenRepository;
import com.task.tracker.authimpl.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HexFormat;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtRefreshTokenProvider {
    private final JwtTokenProperties jwtTokenProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public String generateRefreshToken(String subject) {


        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(
                        Keys.hmacShaKeyFor(jwtTokenProperties.getSecret().getBytes()),
                        SignatureAlgorithm.HS512
                )
                .compact();
    }

    public Claims parseRefreshToken(String refreshToken) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(jwtTokenProperties.getSecret().getBytes()))
                .parseClaimsJws(refreshToken)
                .getBody();
    }

    public void invalidate(String requestRefreshToken) {
        String tokenHash = hash(requestRefreshToken);
        refreshTokenRepository.remove(tokenHash);

    }

    public RefreshToken getAndRemove(String refreshToken) {
        return refreshTokenRepository.getAndRemove(hash(refreshToken));
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to hash refresh token | message={}", e.getMessage());
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}