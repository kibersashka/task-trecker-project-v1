package com.task.tracker.taskimpl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.commonlib.dto.TaskLevelUpEvent;
import com.task.tracker.taskapi.dto.TaskRequest;
import com.task.tracker.taskapi.dto.TaskSearchRequest;
import com.task.tracker.taskapi.dto.TaskSearchResponse;
import com.task.tracker.taskimpl.entity.Task;
import com.task.tracker.taskapi.TaskStatus;
import com.task.tracker.taskimpl.exception.TaskNotFoundException;
import com.task.tracker.taskimpl.kafka.EventPublisher;
import com.task.tracker.taskimpl.kafka.EventPublisherPort;
import com.task.tracker.taskimpl.mapper.TaskMapper;
import com.task.tracker.taskimpl.repository.TaskCriteriaRepository;
import com.task.tracker.taskimpl.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskCriteriaRepository repository;
    private final EventPublisher eventPublisherPort;
    private final ObjectMapper objectMapper;
    private final TaskMapper taskMapper;

    @Transactional
    public Task completeTask(UUID task_id) {
        Task task = taskRepository.findByIdWithTags(task_id)
                .orElseThrow(
                        () -> new TaskNotFoundException(task_id)
                );

        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(Instant.now());

        Task savedTask = taskRepository.save(task);

        try {
            String json = objectMapper.writeValueAsString(
                    new TaskLevelUpEvent(
                            savedTask.getPriority().getXpCount(),
                            savedTask.getAccountId()
                    )
            );
            log.info("Completed task with id {}", json);
            eventPublisherPort.publishLevelUp(
                    json,
                    savedTask.getAccountId()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return savedTask;
    }

    @Transactional
    public Task startTask(UUID task_id) {
        Task task = taskRepository.findByIdWithTags(task_id)
                .orElseThrow(
                        () -> new TaskNotFoundException(task_id)
                );

        task.setStatus(TaskStatus.PROCESSING);
        task.setCompletedAt(Instant.now());

        Task savedTask = taskRepository.save(task);

        return savedTask;
    }

    public List<TaskSearchResponse> search(TaskSearchRequest request) {
        List<Task> tasks = repository.search(
                request.status(),
                request.priority(),
                request.userId(),
                request.dueDate(),
                request.sortBy()
        );

        return tasks.stream().map(
                taskMapper::toResponse
        ).toList();
    }

    public void save(TaskRequest task) {
        Task taskEntity = taskMapper.toEntity(task);
        taskRepository.save(taskEntity);
    }

    @Transactional
    public Task update(TaskRequest task) {
        if (task.id() == null) {
            throw new TaskNotFoundException(null);
        }
        Task taskEntity = taskRepository.findByIdWithTags(task.id())
                .orElseThrow(
                () -> new TaskNotFoundException(task.id())
                );
        log.debug("update {}", taskEntity);
        taskMapper.updateTaskFromRequest(task, taskEntity);

        return taskEntity;
    }

    public void delete(UUID task_id) {
        Task task = taskRepository.findById(task_id)
                .orElseThrow(
                        () -> new TaskNotFoundException(task_id)
                );
        taskRepository.delete(task);
    }
}
