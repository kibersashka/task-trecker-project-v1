package com.task.tracker.notificationimpl.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.commonlib.dto.ReminderCommand;
import com.task.tracker.notificationimpl.emailClient.EmailClient;
import com.task.tracker.notificationimpl.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ReminderListener {
    public static final String REMINDER_TOPIC = "task.reminder.command";

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final EmailClient emailClient;

    @KafkaListener(
            topics = REMINDER_TOPIC,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(String json, Acknowledgment ack) {
        try {
            ReminderCommand event =
                    objectMapper.readValue(json, ReminderCommand.class);

            notificationService.saveForUINotification(
                    event.accountId(),
                    "TASK_DUE_SOON",
                    "Скоро дедлайн",
                    "Задача: " + event.taskName() + " заканчивается " + event.dueTime()
            );

            log.info("Reminder received: {}", event);
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process reminder", e);
        }
    }
}
