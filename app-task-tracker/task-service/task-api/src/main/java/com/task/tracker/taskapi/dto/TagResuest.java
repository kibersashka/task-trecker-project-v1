package com.task.tracker.taskapi.dto;

import java.util.UUID;

public record TagResuest(
        UUID id,
        String name,
        String description,
        String color
) {
}
