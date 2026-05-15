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
            log.info("listenSignUp | {}", json);
            TaskLevelUpEvent accountLevelUpEvent = objectMapper.readValue(json, TaskLevelUpEvent.class);
            accountInfoService.updateXp(accountLevelUpEvent.accountId(), accountLevelUpEvent.xpCount());
            log.debug("Level up Event received: [{}]", accountLevelUpEvent.accountId());
            log.info("ё: [{}]", accountLevelUpEvent);

            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to send level up {} {}", json, ex.getMessage());
            acknowledgment.acknowledge();
        }
    }
}
