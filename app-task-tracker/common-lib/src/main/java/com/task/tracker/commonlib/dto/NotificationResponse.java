package com.task.tracker.commonlib.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String title,
        String type,
        String body
) {
}
