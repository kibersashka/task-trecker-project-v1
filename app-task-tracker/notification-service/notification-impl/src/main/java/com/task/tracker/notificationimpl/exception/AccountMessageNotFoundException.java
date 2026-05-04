package com.task.tracker.notificationimpl.exception;

import java.util.UUID;

public class AccountMessageNotFoundException extends RuntimeException {
    public AccountMessageNotFoundException(UUID message) {
        super("Could not find account message " + message);
    }
}
