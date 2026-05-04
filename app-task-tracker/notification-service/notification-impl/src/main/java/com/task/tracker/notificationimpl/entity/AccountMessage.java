package com.task.tracker.notificationimpl.entity;

import com.task.tracker.commonlib.dto.AccountStatus;
import com.task.tracker.notificationapi.dto.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Table(name = "booking_message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AccountMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private MessageStatus status;

    private AccountStatus accountStatus;

    private String email;

    @Column(name = "send_at")
    private Instant sendAt;

    public static AccountMessage create(String email) {
        return AccountMessage.builder()
                .email(email)
                .status(MessageStatus.AWAITING_DISPATCH)
                .id(UUID.randomUUID())
                .build();
    }
}

