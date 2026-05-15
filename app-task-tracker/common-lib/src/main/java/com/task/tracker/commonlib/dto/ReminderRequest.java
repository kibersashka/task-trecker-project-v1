package com.task.tracker.commonlib.dto;

import java.time.Instant;
import java.util.UUID;

public record ReminderRequest(
        UUID taskId,
        Instant reminderDate
) {
}
