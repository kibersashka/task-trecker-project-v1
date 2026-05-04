package com.task.tracker.notificationimpl.repository;

import com.task.tracker.notificationimpl.entity.AccountMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountMessageRepository extends JpaRepository<AccountMessage, UUID> {
}
