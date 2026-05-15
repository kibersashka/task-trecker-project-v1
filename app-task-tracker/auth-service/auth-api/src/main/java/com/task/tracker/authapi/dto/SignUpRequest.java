package com.task.tracker.authapi.dto;



import com.task.tracker.commonlib.dto.Role;

import java.time.Instant;

public record SignUpRequest(
        String username,
        String rawPassword,
        Role role,
        Instant birthday,
        String email
) {
}
