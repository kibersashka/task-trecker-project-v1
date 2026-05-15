package com.task.tracker.taskapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Tag information")
public record TagResponse(
        UUID id,

        @Schema(description = "Tag name", example = "urgent")
        String name,

        @Schema(description = "Tag color in hex format", example = "#FF5733")
        String color,

        @Schema(description = "Tag description")
        String description

) {}
