package com.task.tracker.taskapi.dto;

import com.task.tracker.taskapi.Priority;
import com.task.tracker.taskapi.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public record TaskRequest(
        /**
         * при create = null
         * при update != null
         */
        UUID id,
        String title,
        String description,
        TaskStatus status,
        Priority priority,
        UUID accountId
) {
}
