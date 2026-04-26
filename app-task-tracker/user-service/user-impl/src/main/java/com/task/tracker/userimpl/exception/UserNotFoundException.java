package com.task.tracker.userimpl.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID message) {
        super("User not found: %s".formatted(message));
    }
}
