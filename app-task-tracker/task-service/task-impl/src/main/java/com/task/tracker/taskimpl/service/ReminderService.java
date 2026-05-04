package com.task.tracker.taskimpl.service;

import com.task.tracker.taskapi.dto.ReminderRequest;
import com.task.tracker.taskapi.dto.ReminderResponse;
import com.task.tracker.taskapi.dto.ReminderStatus;
import com.task.tracker.taskimpl.entity.Reminder;
import com.task.tracker.taskimpl.entity.Task;
import com.task.tracker.taskimpl.exception.TaskNotFoundException;
import com.task.tracker.taskimpl.mapper.ReminderMapper;
import com.task.tracker.taskimpl.repository.ReminderRepository;
import com.task.tracker.taskimpl.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReminderService {
    private final ReminderRepository reminderRepository;
    private final TaskRepository taskRepository;
    private final ReminderMapper reminderMapper;

    @Transactional
    public ReminderResponse save(ReminderRequest reminder) {
        Task task = taskRepository.findById(reminder.taskId())
                .orElseThrow(() -> new TaskNotFoundException(reminder.taskId()));

        if (reminder.reminderDate().isAfter(task.getDueDate())) {
            throw new IllegalArgumentException("Reminder date cannot be after task due date");
        }

        Reminder reminderEntity = reminderMapper.toEntity(reminder);
        reminderEntity.setStatus(ReminderStatus.READY_SEND);
        return reminderMapper.toResponse(reminderRepository.save(reminderEntity));
    }

    @Transactional
    public List<Reminder> getAllReadyToSend(Instant date, ReminderStatus status) {
        return reminderRepository.findReadyReminders(date, status);
    }
}
