package com.task.tracker.taskimpl.controller;

import com.task.tracker.taskapi.api.TaskControllerApi;
import com.task.tracker.taskapi.dto.*;
import com.task.tracker.taskimpl.service.TaskService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController implements TaskControllerApi {

    private final TaskService taskService;

    @Override
    public ResponseEntity<Void> create(@RequestBody TaskRequest request) {
        taskService.save(request);
        log.info("POST /api/tasks: {}", request);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<TaskResponse> update(TaskRequest request) {
        taskService.update(request);
        return ResponseEntity.ok().build();

    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.completeTask(id));
    }

    @Override
    public ResponseEntity<List<TaskSearchResponse>> search(
            @RequestBody TaskSearchRequest request
    ) {
        return ResponseEntity.ok(taskService.search(request));
    }

    @Override
    public ResponseEntity<List<TaskWeeklyResponse>> getWeekly(
            @Parameter(description = "Account (user) ID") @PathVariable UUID userId) {
        return ResponseEntity.ok(taskService.getWeeklyTasks(userId));
    }

    @Override
    public ResponseEntity<TaskResponse> startTask(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.startTask(id));
    }
}
