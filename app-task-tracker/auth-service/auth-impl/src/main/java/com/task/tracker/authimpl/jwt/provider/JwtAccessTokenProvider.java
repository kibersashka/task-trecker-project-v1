package com.task.tracker.authimpl.jwt.provider;

import com.task.tracker.authimpl.entity.Account;
import com.task.tracker.authimpl.exception.AuthenticationHeaderException;
import com.task.tracker.authimpl.jwt.properties.JwtTokenProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAccessTokenProvider {

    private final JwtTokenProperties jwtTokenProperties;

    private static final String ROLES = "roles";


    public String generateAccessToken(Account account) {

        Claims claims = Jwts.claims().setSubject(account.getUsername());
        claims.put(ROLES, account.getRoles());
        claims.put("id", account.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plusMillis(jwtTokenProperties.getExpiresAt())))
                .signWith(
                        Keys.hmacShaKeyFor(jwtTokenProperties.getSecret().getBytes()),
                        SignatureAlgorithm.HS512
                )
                .compact();
    }

    public boolean validateAccessToken(String accessToken, String subject)
            throws AuthenticationHeaderException {

        try {
            Claims claims = parseAccessToken(accessToken);
            String subjectFromToken = claims.getSubject();

            return subject.equals(subjectFromToken);

        } catch (ExpiredJwtException e) {
            throw new AuthenticationHeaderException("Token expired date error");
        }
    }

    public Claims parseAccessToken(String token) {

        try {

            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtTokenProperties.getSecret().getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (JwtException | IllegalArgumentException e) {

            throw new AuthenticationHeaderException("Invalid access token");

        }
    }

    public List<String> getRolesFromAccessToken(String accessToken) {
        try {
            return (List<String>) Jwts.parser()
                    .setSigningKey(jwtTokenProperties.getSecret())
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .get(ROLES);

        } catch (ExpiredJwtException e) {
            return (List<String>) e.getClaims().get(ROLES);
        }
    }

    public String getSubjectFromAccessToken(String accessToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtTokenProperties.getSecret())
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();

        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }
}