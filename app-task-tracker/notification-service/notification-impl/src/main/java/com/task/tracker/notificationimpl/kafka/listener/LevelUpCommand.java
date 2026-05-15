package com.task.tracker.notificationimpl.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.commonlib.dto.AccountLevelUpEvent;
import com.task.tracker.commonlib.dto.ReminderCommand;
import com.task.tracker.notificationimpl.emailClient.EmailClient;
import com.task.tracker.notificationimpl.entity.AccountMessage;
import com.task.tracker.notificationimpl.service.AccountMessageService;
import com.task.tracker.notificationimpl.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class LevelUpCommand {
    public static final String ACCOUNT_LEVEL_UP = "account.level.up.command";

    private final AccountMessageService accountMessageService;
    private final ObjectMapper objectMapper;
    private final EmailClient emailClient;

    @KafkaListener(
            topics = ACCOUNT_LEVEL_UP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(String json, Acknowledgment ack) {
        try {
            AccountLevelUpEvent command = objectMapper.readValue(json, AccountLevelUpEvent.class);

            UUID messageId = accountMessageService.saveForEmailNotification(
                    command
            );
            log.info("listenSignUp | {}", json);

            emailClient.sendEmail(
                    command.email(),
                    "Account Level Up",
                    "Ваш уровень повышен до %s".formatted(command.accountStatus())
            );

            accountMessageService.makeAsSend(messageId);
            ack.acknowledge();
            log.info("Reminder received: {}", json);

        } catch (Exception e) {
            ack.acknowledge();
            log.error("Failed to process reminder", e);
        }
    }
}
