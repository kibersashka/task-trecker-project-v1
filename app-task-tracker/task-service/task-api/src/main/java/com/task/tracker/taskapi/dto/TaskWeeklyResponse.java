package com.task.tracker.taskapi.dto;

import com.task.tracker.commonlib.dto.Priority;
import com.task.tracker.commonlib.dto.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Task summary for the weekly view (tags excluded)")
public record TaskWeeklyResponse(

        @Schema(description = "Task ID")
        UUID id,

        @Schema(description = "Task title")
        String title,

        @Schema(description = "Task description")
        String description,

        @Schema(description = "Task status")
        TaskStatus status,

        @Schema(description = "Task priority")
        Priority priority,

        @Schema(description = "Account (user) ID")
        UUID accountId,

        @Schema(description = "Created at timestamp")
        Instant createdAt,

        @Schema(description = "Due date")
        Instant dueDate

) {}
