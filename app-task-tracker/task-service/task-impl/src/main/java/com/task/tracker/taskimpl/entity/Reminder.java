package com.task.tracker.taskimpl.entity;

import com.task.tracker.taskapi.dto.ReminderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    @Column(name = "reminder_time")
    private Instant reminderTime;

    @Enumerated(EnumType.STRING)
    private ReminderStatus status;
}
