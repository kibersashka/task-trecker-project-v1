package com.task.tracker.taskapi.dto;

import com.task.tracker.commonlib.dto.Priority;
import com.task.tracker.commonlib.dto.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Request DTO for creating or updating a task")
public record TaskRequest(

        @Schema(description = "Task ID — null when creating, required when updating")
        UUID id,

        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        @Schema(description = "Task title", example = "Write unit tests")
        String title,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        @Schema(description = "Task description", example = "Cover service layer with JUnit 5 tests")
        String description,

        @Schema(description = "Task status")
        TaskStatus status,

        @NotNull(message = "Priority must not be null")
        @Schema(description = "Task priority")
        Priority priority,

        @NotNull(message = "Account ID must not be null")
        @Schema(description = "Account (user) ID")
        UUID accountId,

        @Schema(description = "Task due date", example = "2026-12-31T23:59:00Z")
        Instant dueDate,

        @Schema(description = "Set of tag IDs to associate with the task")
        Set<UUID> tagIds

) {}
