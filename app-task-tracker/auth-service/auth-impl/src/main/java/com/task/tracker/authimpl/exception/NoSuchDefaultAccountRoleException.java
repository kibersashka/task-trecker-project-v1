package com.task.tracker.authimpl.exception;

import com.task.tracker.commonlib.dto.Role;

public class NoSuchDefaultAccountRoleException extends RuntimeException {
    public NoSuchDefaultAccountRoleException(Role role) {
        super("No default account role found with name %s".formatted(role));
    }
}
