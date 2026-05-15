package com.task.tracker.taskapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Request DTO for creating or updating a tag")
public record TagRequest(

        @Schema(description = "Tag ID — null for create, required for update")
        UUID id,

        @NotBlank(message = "Tag name must not be blank")
        @Size(max = 100, message = "Tag name must not exceed 100 characters")
        @Schema(description = "Tag name", example = "urgent")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        @Schema(description = "Tag description", example = "High priority tasks")
        String description,

        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
                message = "Color must be a valid hex code, e.g. #FF5733")
        @Schema(description = "Tag color in hex format", example = "#FF5733")
        String color,

        @NotNull(message = "Account ID must not be null")
        @Schema(description = "Account (user) ID")
        UUID accountId

) {}
