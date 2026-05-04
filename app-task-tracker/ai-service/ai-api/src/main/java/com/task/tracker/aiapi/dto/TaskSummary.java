package com.task.tracker.aiapi.dto;

import java.time.Instant;
import java.util.UUID;

public record TaskSummary(
        UUID id,
        String title,
        String description,
        String priority,
        String status,
        Instant dueDate
) {
}
