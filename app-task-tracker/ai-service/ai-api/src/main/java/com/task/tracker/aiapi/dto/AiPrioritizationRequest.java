package com.task.tracker.aiapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record AiPrioritizationRequest(
        @NotNull
        UUID accountId,

        @NotEmpty
        @Size(max = 50, message = "Максимум 50 задач за один запрос")
        List<TaskSummary> tasks
) {
}