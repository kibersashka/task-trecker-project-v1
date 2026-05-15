package com.task.tracker.commonlib.dto;

import java.time.Instant;
import java.util.UUID;

public record ReminderResponse(
        UUID taskId,
        UUID reminderId,
        Instant reminderDate
) {
}
