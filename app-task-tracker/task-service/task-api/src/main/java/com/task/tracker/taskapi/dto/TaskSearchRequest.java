package com.task.tracker.taskapi.dto;

import com.task.tracker.taskapi.Priority;
import com.task.tracker.taskapi.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TaskSearchRequest(
        TaskStatus status,
        Priority priority,
        UUID userId,
        Instant dueDate,
        List<String> sortBy
) {
}
