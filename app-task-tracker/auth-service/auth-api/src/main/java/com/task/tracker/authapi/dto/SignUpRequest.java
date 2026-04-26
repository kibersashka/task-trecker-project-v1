package com.task.tracker.authapi.dto;

import com.task.tracker.authapi.status.Role;

import java.time.Instant;

public record SignUpRequest(
        String username,
        String rawPassword,
        Role role,
        Instant birthday,
        String email
) {
}
