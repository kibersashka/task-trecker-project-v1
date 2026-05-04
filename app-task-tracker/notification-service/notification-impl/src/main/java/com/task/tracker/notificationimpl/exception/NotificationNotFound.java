package com.task.tracker.notificationimpl.exception;

import java.util.UUID;

public class NotificationNotFound extends RuntimeException {
    public NotificationNotFound(UUID message) {
        super("Notification not found for message " + message);
    }
}
