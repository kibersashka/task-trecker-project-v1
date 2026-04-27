package com.task.tracker.taskapi.dto;

public record TagResponse(
        String name,
        String color,
        String description
) {
}
