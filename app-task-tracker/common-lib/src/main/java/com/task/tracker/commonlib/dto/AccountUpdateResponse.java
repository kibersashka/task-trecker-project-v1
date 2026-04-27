package com.task.tracker.commonlib.dto;

public record AccountUpdateResponse(
        String email,
        Integer xpCount
) {
}
