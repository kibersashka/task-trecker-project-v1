package com.task.tracker.userimpl.kafka.port;

import java.util.UUID;

public interface EventPublisherPort {

    String TASK_LEVEL_UP_COMMAND = "account.level.up.command";

    void publish(String topic, String key, Object payload);

    default void publishLevelUp(
            String jsonPayload,
            UUID user_id
    ) {

        publish(
                TASK_LEVEL_UP_COMMAND,
                user_id.toString(),
                jsonPayload
        );
    }
}