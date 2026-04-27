package com.task.tracker.taskimpl.exception;

import java.util.UUID;

public class TagExistsException extends RuntimeException {
    public TagExistsException(UUID message) {
        super("No tag with id " + message.toString());
    }
}
