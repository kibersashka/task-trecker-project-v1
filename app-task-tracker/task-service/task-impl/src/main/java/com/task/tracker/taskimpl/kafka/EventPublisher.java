package com.task.tracker.taskimpl.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher implements EventPublisherPort {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    @Override
    public void publish(String topic, String key, String payload) {
        log.debug("Publishing event for topic [{}] and key [{}]", topic, key);

            kafkaTemplate.send(
                    topic,
                    key,
                    payload
            );

    }
}
