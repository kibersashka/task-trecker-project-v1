package com.task.tracker.commonlib.dto;


import java.time.Instant;
import java.util.UUID;

public record ReminderCommand(
        UUID accountId,
        String taskName,
        Instant dueTime
) {
}
