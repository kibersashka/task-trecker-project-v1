package com.task.tracker.authapi.dto;



import com.task.tracker.commonlib.dto.Role;

import java.util.Set;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String username,
        Set<Role> roles
) {
}
