package com.task.tracker.commonlib.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        String title,
        String type,
        String body
) {
}
