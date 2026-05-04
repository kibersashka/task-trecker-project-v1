package com.task.tracker.userimpl.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.commonlib.dto.TaskLevelUpEvent;
import com.task.tracker.userimpl.kafka.port.EventPublisher;
import com.task.tracker.userimpl.service.AccountInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountLevelUpCommandConsumer {
    private final AccountInfoService accountInfoService;
    private final ObjectMapper objectMapper;
    private final EventPublisher eventPublisher;

    @KafkaListener(
            topics = "task.level.up.command",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenSignUp(
            String json,
            Acknowledgment acknowledgment
    ) {
        try {
            TaskLevelUpEvent accountLevelUpEvent = objectMapper.readValue(json, TaskLevelUpEvent.class);
            accountInfoService.updateXp(accountLevelUpEvent.account_id(), accountLevelUpEvent.xpCount());
            log.debug("Sign Up Event received: [{}]", accountLevelUpEvent.account_id());
            log.info("ё: [{}]", accountLevelUpEvent);

            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to send sign up {}", json);
            acknowledgment.acknowledge();
        }
    }
}
