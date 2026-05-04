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
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class LevelUpCommand {
    public static final String ACCOUNT_LEVEL_UP = "account.level.up.reminder";

    private final AccountMessageService accountMessageService;
    private final ObjectMapper objectMapper;
    private final EmailClient emailClient;

    @KafkaListener(
            topics = ACCOUNT_LEVEL_UP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(String json) {
        try {
            AccountLevelUpEvent command = objectMapper.readValue(json, AccountLevelUpEvent.class);

            UUID messageId = accountMessageService.saveForEmailNotification(
                    command
            );

            emailClient.sendEmail(
                    command.email(),
                    "Account Level Up",
                    "Ваш уровень повышен до %s".formatted(command.accountStatus())
            );

            accountMessageService.makeAsSend(messageId);
            log.info("Reminder received: {}", json);

        } catch (Exception e) {
            log.error("Failed to process reminder", e);
        }
    }
}
