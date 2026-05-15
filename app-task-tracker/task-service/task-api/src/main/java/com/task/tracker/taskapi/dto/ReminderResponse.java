package com.task.tracker.taskapi.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Reminder details")
public record ReminderResponse(

        @Schema(description = "ID of the associated task")
        UUID taskId,

        @Schema(description = "Reminder ID")
        UUID reminderId,

        @Schema(description = "Scheduled reminder time")
        Instant reminderDate

) {}