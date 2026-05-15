package com.task.tracker.taskapi.dto;

import com.task.tracker.commonlib.dto.Priority;
import com.task.tracker.commonlib.dto.TaskStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record TaskSearchResponse(
        UUID id,
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
