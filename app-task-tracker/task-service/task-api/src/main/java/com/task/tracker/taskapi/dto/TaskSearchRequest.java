package com.task.tracker.taskapi.dto;

import com.task.tracker.commonlib.dto.Priority;
import com.task.tracker.commonlib.dto.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Search/filter request for tasks")
public record TaskSearchRequest(

        @Schema(description = "Filter by task status")
        TaskStatus status,

        @Schema(description = "Filter by priority")
        Priority priority,

        @NotNull(message = "User ID must not be null")
        @Schema(description = "Filter by account (user) ID", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,

        @Schema(description = "Filter by exact due date")
        Instant dueDate,

        @Schema(description = "Fields to sort by: status, priority, createdAt, dueDate, tagName",
                example = "[\"priority\", \"dueDate\"]")
        List<String> sortBy,

        @Schema(description = "Filter by one tag name",
                example = "[\"work\", \"school\"]")
        String tagName

) {}
