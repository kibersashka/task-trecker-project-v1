package com.task.tracker.authimpl.jwt;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtTokenProperties {
    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expires.at}")
    private Long expiresAt;
}
