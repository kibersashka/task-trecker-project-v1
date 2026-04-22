package com.task.tracker.authimpl.exception;

public class JwtNotValidException extends RuntimeException {
    public JwtNotValidException(String key) {
        super("Refresh token not found for key: %s".formatted(key));
    }
}
