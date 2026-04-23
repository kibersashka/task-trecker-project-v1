package com.task.tracker.authapi.dto;

import com.task.tracker.authapi.status.Role;

import java.util.Set;
import java.util.UUID;

public record UpdateRequest(
        UUID id,
        String username,
        Role role
) {
}
