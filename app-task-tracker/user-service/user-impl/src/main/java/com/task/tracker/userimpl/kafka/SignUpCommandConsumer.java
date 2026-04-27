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
        SignUpEvent signUpEvent = null;
        try {
            signUpEvent = objectMapper.readValue(json, SignUpEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.debug("Sign Up Event received: [{}]", signUpEvent.account_id());

        try {
            AccountInfo accountInfo = AccountInfo.create(
                    signUpEvent.account_id(),
                    signUpEvent.email()
            );
            accountInfoService.save(accountInfo);

            acknowledgment.acknowledge();
            log.debug("Sign Up Event received:!!!!! [{}]", signUpEvent.account_id());
        } catch (Exception e) {
            log.error("Failed to send sign up {}", signUpEvent.email());
            throw e;
        }
    }
}
