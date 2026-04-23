package com.task.tracker.authimpl.exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {
        super("Too many generate refresh token requests");
    }
}
