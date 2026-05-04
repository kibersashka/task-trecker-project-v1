package com.task.tracker.notificationimpl.mapper;

import com.task.tracker.commonlib.dto.NotificationResponse;
import com.task.tracker.notificationimpl.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }

        return new NotificationResponse(
                notification.getTitle(),
                notification.getType(),
                notification.getBody()
        );
    }
}
