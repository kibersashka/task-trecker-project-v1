package com.task.tracker.commonlib.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Standardised error response")
public record ErrorResponse(

        @Schema(description = "HTTP status code")
        int status,

        @Schema(description = "Error message")
        String message,

        @Schema(description = "Field-level validation errors (field → message)")
        Map<String, String> fieldErrors,

        @Schema(description = "Timestamp of the error")
        Instant timestamp

) {}

