package com.task.tracker.authimpl.exception;

public class AuthenticationHeaderException extends RuntimeException {
    public AuthenticationHeaderException(String token) {
        super("Exception with token: %s".formatted(token));
    }
}
