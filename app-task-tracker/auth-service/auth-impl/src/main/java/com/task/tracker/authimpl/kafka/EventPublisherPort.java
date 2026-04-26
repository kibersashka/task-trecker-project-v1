package com.task.tracker.authimpl.kafka;

import java.util.UUID;

public interface EventPublisherPort {

    String SIGN_UP_SUCCEEDED_COMMAND = "auth.sing.up.command";

    void publish(String topic, String key, Object payload);

    default void publishSingUpSuccess(
            String jsonPayload,
            UUID user_id
    ) {

        publish(
                SIGN_UP_SUCCEEDED_COMMAND,
                user_id.toString(),
                jsonPayload
        );
    }

}