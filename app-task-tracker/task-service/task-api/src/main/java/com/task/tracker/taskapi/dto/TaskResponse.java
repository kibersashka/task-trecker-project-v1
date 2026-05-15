package com.task.tracker.taskapi.dto;
import com.task.tracker.commonlib.dto.Priority;
import com.task.tracker.commonlib.dto.TaskStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Task response DTO")
public record TaskResponse(

        @Schema(description = "Task ID")
        UUID id,

        @NotBlank
        @Size(max = 255)
        @Schema(description = "Task title", example = "Write unit tests")
        String title,

        @Size(max = 2000)
        @Schema(description = "Task description",
                example = "Cover service layer with JUnit 5 tests")
        String description,

        @NotNull
        @Schema(description = "Task status")
        TaskStatus status,

        @NotNull
        @Schema(description = "Task priority")
        Priority priority,

        @NotNull
        @Schema(description = "Account ID")
        UUID accountId,

        @Schema(description = "Task creation timestamp")
        Instant createdAt,

        @Schema(description = "Task completion timestamp")
        Instant completedAt,

        @Schema(description = "Task last update timestamp")
        Instant updatedAt,

        @Schema(description = "Task due date")
        Instant dueDate,

        @ArraySchema(
                schema = @Schema(description = "Associated tag IDs")
        )
        Set<UUID> tagIds

) {}
