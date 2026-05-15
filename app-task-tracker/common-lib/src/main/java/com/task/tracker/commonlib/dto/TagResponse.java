package com.task.tracker.commonlib.dto;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String name,
        String color,
        String description
) {
}
