package com.task.tracker.notificationimpl.service;

import com.task.tracker.commonlib.dto.NotificationResponse;
import com.task.tracker.notificationapi.dto.MessageStatus;
import com.task.tracker.notificationimpl.emailClient.EmailClient;
import com.task.tracker.notificationimpl.entity.Notification;
import com.task.tracker.notificationimpl.exception.NotificationNotFound;
import com.task.tracker.notificationimpl.mapper.NotificationMapper;
import com.task.tracker.notificationimpl.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailClient emailClient;

    public void saveForUINotification(UUID userId, String type, String title, String body) {
        Notification notification = Notification.builder()
                .accountId(userId)
                .type(type)
                .title(title)
                .body(body)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> findAllByAccountId(UUID accountId) {

        return notificationRepository.findAllByAccountIdAndStatus(
                accountId,
                MessageStatus.AWAITING_DISPATCH
                )
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Transactional
    public void markAsRead(UUID accountId, UUID notificationId) {
        Notification notification = notificationRepository.findByAccountIdAndIdAndStatus(accountId, notificationId, MessageStatus.AWAITING_DISPATCH)
                .orElseThrow(
                        () -> new NotificationNotFound(accountId)
                );

        notification.setStatus(MessageStatus.SENT);
    }
}
