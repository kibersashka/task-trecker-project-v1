package com.task.tracker.taskimpl.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(UUID message) {
        super("task not found: %s".formatted(message));
    }
}
