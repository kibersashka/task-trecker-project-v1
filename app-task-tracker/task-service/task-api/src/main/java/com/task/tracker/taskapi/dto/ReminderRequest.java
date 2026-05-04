package com.task.tracker.taskapi.dto;

import java.time.Instant;
import java.util.UUID;

public record ReminderRequest(
        UUID taskId,
        Instant reminderDate
) {
}
