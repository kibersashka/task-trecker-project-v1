package com.task.tracker.authapi.dto;

public record TokenCouple(
        String accessToken,
        String refreshToken,
        Long refreshTokenExpiration
) {
}
