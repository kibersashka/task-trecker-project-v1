package com.task.tracker.commonlib.dto;

import java.time.Instant;
import java.util.UUID;

public record ReminderEvent(
        UUID accountId,
        String title,
        String description,
        String body,
        Instant dueDate
) {
}
