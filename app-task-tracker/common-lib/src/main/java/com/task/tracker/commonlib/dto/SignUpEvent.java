package com.task.tracker.commonlib.dto;


import java.time.Instant;
import java.util.UUID;

public record SignUpEvent(
        UUID accountId,
        String email,
        String username
) {
}
