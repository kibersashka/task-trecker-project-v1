package com.task.tracker.authimpl.exception;

import com.task.tracker.authapi.status.Role;

public class NoSuchDefaultAccountRoleException extends RuntimeException {
    public NoSuchDefaultAccountRoleException(Role role) {
        super("No default account role found with name %s".formatted(role));
    }
}
