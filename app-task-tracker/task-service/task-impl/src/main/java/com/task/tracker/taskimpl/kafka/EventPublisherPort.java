package com.task.tracker.taskimpl.kafka;

import java.util.UUID;

public interface EventPublisherPort {

    String TASK_LEVEL_UP_COMMAND = "task.level.up.command";

    void publish(String topic, String key, String payload);

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