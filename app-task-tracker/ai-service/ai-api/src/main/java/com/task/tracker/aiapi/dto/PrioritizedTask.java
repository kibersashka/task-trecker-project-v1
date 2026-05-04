package com.task.tracker.aiapi.dto;

import java.util.UUID;

public record PrioritizedTask(
        UUID taskId,
        int recommendedOrder,
        String suggestedPriority,
        String reason
) {
}