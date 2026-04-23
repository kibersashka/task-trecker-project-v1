package com.task.tracker.authapi.dto;

import com.task.tracker.authapi.status.Role;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String username,
        Set<Role> roles
) {
}
