package com.task.tracker.commonlib.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record TaskSearchResponse(
        String title,
        String description,
        TaskStatus status,
        Priority priority,
        UUID accountId,
        Instant createdAt,
        Instant dueDate,
        Set<TagResponse> tags
) {
}
