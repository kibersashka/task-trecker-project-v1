package com.task.tracker.userimpl.entity;

import com.task.tracker.commonlib.dto.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private AccountStatus accountStatus;

    public static AccountInfo create(
            UUID account_id,
            String email
    ) {
        return AccountInfo.builder()
                .accountStatus(AccountStatus.INTER)
                .email(email)
                .id(account_id)
                .xp(0)
                .build();
    }
}
