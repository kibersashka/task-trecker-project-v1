package com.task.tracker.authapi.dto;
import com.task.tracker.commonlib.dto.Role;

import java.util.UUID;

public record UpdateRequest(
        UUID id,
        String username,
        Role role
) {
}
