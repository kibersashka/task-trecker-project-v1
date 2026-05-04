package com.task.tracker.taskimpl.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.commonlib.dto.ReminderCommand;
import com.task.tracker.taskapi.dto.ReminderStatus;
import com.task.tracker.taskimpl.entity.Reminder;
import com.task.tracker.taskimpl.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class ReminderScheduler {
    public static final String REMINDER_TOPIC = "task.reminder.command";
    private final ReminderService reminderService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void sendReminder() {
        Instant to = Instant.now();

        List<Reminder> reminders = reminderService.getAllReadyToSend(to, ReminderStatus.READY_SEND);
        for (Reminder reminder : reminders) {

            ReminderCommand command = new ReminderCommand(
                    reminder.getTask().getAccountId(),
                    reminder.getTask().getTitle(),
                    reminder.getTask().getDueDate()
            );

            try {
                String saved = objectMapper.writeValueAsString(command);
                kafkaTemplate.send(
                        REMINDER_TOPIC,
                        saved
                );
                reminder.setStatus(ReminderStatus.SEND);
                log.info("Sent reminder command: {}", saved);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
