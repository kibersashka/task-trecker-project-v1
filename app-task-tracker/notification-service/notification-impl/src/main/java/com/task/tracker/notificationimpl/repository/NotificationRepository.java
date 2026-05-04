package com.task.tracker.notificationimpl.repository;

import com.task.tracker.notificationapi.dto.MessageStatus;
import com.task.tracker.notificationimpl.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByAccountIdAndStatus(
            UUID accountId,
            MessageStatus status
    );

    Optional<Notification> findByAccountIdAndIdAndStatus(UUID accountId, UUID id, MessageStatus status);
}
