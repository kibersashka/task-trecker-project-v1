package com.task.tracker.taskapi.dto;

import java.time.Instant;
import java.util.UUID;

public record ReminderResponse(
        UUID taskId,
        UUID reminderId,
        Instant reminderDate
) {
}
