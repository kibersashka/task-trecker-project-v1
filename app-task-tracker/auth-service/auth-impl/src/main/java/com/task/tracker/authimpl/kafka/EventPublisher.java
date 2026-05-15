package com.task.tracker.authimpl.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher implements EventPublisherPort{
    private final KafkaTemplate<String, String> kafkaTemplate;
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
