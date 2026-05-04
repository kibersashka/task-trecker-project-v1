package com.task.tracker.commonlib.dto;

public record AccountLevelUpEvent(
        String email,
        AccountStatus accountStatus
) {
}
