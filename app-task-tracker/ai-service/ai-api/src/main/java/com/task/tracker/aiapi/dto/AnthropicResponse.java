package com.task.tracker.aiapi.dto;

public record AnthropicResponse(
        String text,
        int inputTokens,
        int outputTokens
) {
}
