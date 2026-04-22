package com.task.tracker.authimpl.repository;

import com.task.tracker.authimpl.entity.Account;
import com.task.tracker.authimpl.entity.RefreshToken;
import com.task.tracker.authimpl.exception.JwtNotValidException;
import com.task.tracker.authimpl.jwt.JwtTokenProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, RefreshToken> redisTemplate;
    private final RedisTemplate<String, String> redisSetTemplate;
    private final JwtTokenProperties jwtTokenProperties;

    private static final String TOKEN_KEY = "refresh_token:%s";
    private static final String SESSION_KEY = "account_sessions:%s";

    public void save(String tokenHash, RefreshToken refreshToken) {
        String key = String.format(TOKEN_KEY, tokenHash);
        String sessionKey = String.format(SESSION_KEY, tokenHash);
        log.info(String.format("Removed refresh token save for key: %s", key));

        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                Duration.ofMillis(jwtTokenProperties.getExpiresAt())
        );

        redisSetTemplate.opsForZSet().add(
                sessionKey,
                tokenHash,
                refreshToken.getCreatedAt().getEpochSecond()
        );
    }

    public RefreshToken getAndRemove(String tokenHash) {
        String key = String.format(TOKEN_KEY, tokenHash);
        RefreshToken refreshToken = redisTemplate.opsForValue().getAndDelete(key);
        log.info(String.format("Removed refresh token get and remove for key: %s", key));

        if (refreshToken == null) {
            log.warn(String.format("Refresh token not found for key: %s", key));
            throw new JwtNotValidException(key);
        }

        String sessionKey = SESSION_KEY.formatted(refreshToken.getAccountId());
        redisSetTemplate.opsForZSet().remove(sessionKey, tokenHash);

        return refreshToken;

    }

    public void remove(String tokenHash) {
        String key = String.format(TOKEN_KEY, tokenHash);
        RefreshToken refreshToken = redisTemplate.opsForValue().getAndDelete(key);
        log.info(String.format("Removed refresh token for key: %s", key));

        if (refreshToken != null) {
            String sessionKey = SESSION_KEY.formatted(refreshToken.getAccountId());
            redisSetTemplate.opsForZSet().remove(sessionKey, tokenHash);
            return;
        }
        log.warn(String.format("Refresh token not found for key: %s", key));
    }

    public List<RefreshToken> getAllTokenByAccountId(UUID accountId) {
        String sessionKey = SESSION_KEY.formatted(accountId);

        List<RefreshToken> tokens = new ArrayList<>();
        Set<String> tokenHashSet = redisSetTemplate.opsForZSet().range(sessionKey, 0, -1);

        if (tokenHashSet.isEmpty()) {
            log.warn(String.format("Refresh token not found for accountId: %s", accountId));
            return tokens;
        }

        for (String tokenHash : tokenHashSet) {
            String key = String.format(TOKEN_KEY, accountId);

            RefreshToken refreshToken = redisTemplate.opsForValue().get(key);

            if (refreshToken == null) {
                redisSetTemplate.opsForZSet().remove(sessionKey, tokenHash);
                continue;
            }
            tokens.add(refreshToken);

        }

        return tokens;

    }

    public int getCountByAccount(Account account) {
        return getAllTokenByAccountId(account.getId()).size();
    }

}

