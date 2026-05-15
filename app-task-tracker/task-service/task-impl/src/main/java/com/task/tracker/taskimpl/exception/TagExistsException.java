package com.task.tracker.taskimpl.exception;

import java.util.UUID;

public class TagExistsException extends RuntimeException {
    public TagExistsException(UUID id) {
        super(id != null
                ? "Tag already exists with id: " + id
                : "Tag with this name and description already exists");
    }
}
