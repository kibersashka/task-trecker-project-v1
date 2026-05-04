package com.task.tracker.commonlib.dto;

import java.util.UUID;

public record TaskLevelUpEvent(
        Integer xpCount,
        UUID account_id
) {
}
