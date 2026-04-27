package com.task.tracker.taskapi.dto;

import com.task.tracker.taskapi.Priority;
import com.task.tracker.taskapi.TaskStatus;

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
