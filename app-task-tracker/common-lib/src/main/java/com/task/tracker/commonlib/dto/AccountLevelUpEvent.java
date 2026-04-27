package com.task.tracker.commonlib.dto;

import java.util.UUID;

public record AccountLevelUpEvent(
        Integer xpCount,
        UUID account_id
) {
}
