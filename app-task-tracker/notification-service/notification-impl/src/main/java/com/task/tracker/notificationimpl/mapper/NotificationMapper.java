package com.task.tracker.notificationimpl.mapper;

import com.task.tracker.commonlib.dto.NotificationResponse;
import com.task.tracker.notificationimpl.entity.Notification;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);
}

