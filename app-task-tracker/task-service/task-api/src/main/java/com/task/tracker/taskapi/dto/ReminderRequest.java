package com.task.tracker.taskapi.dto;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request DTO for creating a reminder")
public record ReminderRequest(

        @NotNull(message = "Task ID must not be null")
        @Schema(description = "ID of the task to set a reminder for")
        UUID taskId,

        @NotNull(message = "Reminder date must not be null")
        @Future(message = "Reminder date must be in the future")
        @Schema(description = "When to send the reminder (ISO-8601)", example = "2026-12-01T09:00:00Z")
        Instant reminderDate

) {}

