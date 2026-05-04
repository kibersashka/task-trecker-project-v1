package com.task.tracker.aiapi.dto;

import java.util.List;
import java.util.UUID;

public record AiPrioritizationResponse(
        UUID requestId,
        List<PrioritizedTask> recommendations,
        String generalAdvice,
        int inputTokens,
        int outputTokens
) {
}