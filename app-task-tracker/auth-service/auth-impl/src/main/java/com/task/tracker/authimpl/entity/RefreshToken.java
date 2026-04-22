package com.task.tracker.authimpl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private UUID accountId;
    private Instant createdAt = Instant.now();
}
