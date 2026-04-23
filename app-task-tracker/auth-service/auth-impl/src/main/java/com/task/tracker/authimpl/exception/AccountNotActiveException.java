package com.task.tracker.authimpl.exception;

import java.util.UUID;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(UUID id) {
        super("Account not active | accountId=" + id);
    }
}
