package com.task.tracker.taskimpl.controller;

import com.task.tracker.taskapi.dto.TaskRequest;
import com.task.tracker.taskapi.dto.TaskSearchRequest;
import com.task.tracker.taskapi.dto.TaskSearchResponse;
import com.task.tracker.taskimpl.entity.Task;
import com.task.tracker.taskimpl.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody TaskRequest request) {
        taskService.save(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Task> update(@RequestBody TaskRequest request) {
        Task updated = taskService.update(request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Task> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.completeTask(id));
    }

    @PostMapping("/search")
    public ResponseEntity<List<TaskSearchResponse>> search(
            @RequestBody TaskSearchRequest request
    ) {
        return ResponseEntity.ok(taskService.search(request));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Task> startTask(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.startTask(id));
    }
}
