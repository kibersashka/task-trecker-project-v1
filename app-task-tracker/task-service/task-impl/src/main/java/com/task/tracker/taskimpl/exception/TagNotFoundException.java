package com.task.tracker.taskimpl.exception;

import java.util.UUID;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(UUID message) {
        super("No tag with id " + message.toString());
    }
}
