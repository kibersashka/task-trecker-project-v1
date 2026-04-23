package com.task.tracker.authapi.dto;

import com.task.tracker.authapi.status.Role;

public record SignInRequest(
        String username,
        String rawPassword,
        Role role
) {
}
