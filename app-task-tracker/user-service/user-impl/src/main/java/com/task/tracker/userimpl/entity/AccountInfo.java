package com.task.tracker.userimpl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account_info")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {

    @Id
    private UUID id;

    private String email;

    private Integer xp;

    public static AccountInfo create(
            UUID account_id,
            String email
    ) {
        return AccountInfo.builder()
                .email(email)
                .id(account_id)
                .xp(0)
                .build();
    }

}
