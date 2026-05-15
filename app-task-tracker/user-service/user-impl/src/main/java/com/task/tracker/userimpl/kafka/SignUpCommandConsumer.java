package com.task.tracker.userimpl.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.commonlib.dto.SignUpEvent;
import com.task.tracker.userimpl.entity.AccountInfo;
import com.task.tracker.userimpl.service.AccountInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpCommandConsumer {
    private final AccountInfoService accountInfoService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "auth.sing.up.command",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenSignUp(
            String json,
            Acknowledgment acknowledgment
    ) {
        log.info("listenSignUp | {}", json);
        SignUpEvent signUpEvent = null;
        try {
            signUpEvent = objectMapper.readValue(json, SignUpEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.debug("Sign Up Event received: [{}]", signUpEvent.accountId());

        try {
            AccountInfo accountInfo = AccountInfo.create(
                    signUpEvent.accountId(),
                    signUpEvent.email(),
                    signUpEvent.username()
            );
            accountInfoService.save(accountInfo);

            acknowledgment.acknowledge();
        } catch (Exception e) {
            acknowledgment.acknowledge();
            log.error("Failed to send sign up {}", signUpEvent.email());
            throw e;
        }
    }
}
