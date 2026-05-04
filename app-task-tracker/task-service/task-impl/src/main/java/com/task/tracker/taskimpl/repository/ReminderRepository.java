package com.task.tracker.taskimpl.repository;


import com.task.tracker.taskapi.dto.ReminderStatus;
import com.task.tracker.taskimpl.entity.Reminder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    @Query("""
    select r from Reminder r
    join fetch r.task t
    where r.reminderTime <= CURRENT_TIMESTAMP
    and r.status = :status
    """)
    List<Reminder> findReadyReminders(@Param("now") Instant now, @Param("status") ReminderStatus status);
}
