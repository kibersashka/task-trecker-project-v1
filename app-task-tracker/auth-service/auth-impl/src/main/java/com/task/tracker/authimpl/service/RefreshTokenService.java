package com.task.tracker.authimpl.service;

import com.task.tracker.authimpl.entity.Account;
import com.task.tracker.authimpl.exception.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    public RefreshTokenService(@Qualifier("stringStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    private static final String KEY_LOCK_SESSION = "session_lock:%s";

    public void check(Account account) {
        String lockSession = KEY_LOCK_SESSION.formatted(account.getId());
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockSession, "1", Duration.ofSeconds(5));

        if (Boolean.FALSE.equals(locked)) {
            log.warn("Too many generate refresh token request | accountId={}", account.getId());
            throw new TooManyRequestsException();
        }
    }

    public void deleteLockKey(Account account) {
        String lockSession = KEY_LOCK_SESSION.formatted(account.getId());
        redisTemplate.delete(lockSession);
    }

}
