package com.task.tracker.taskapi.api;

import com.task.tracker.commonlib.dto.ErrorResponse;
import com.task.tracker.taskapi.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management operations")
public interface TaskControllerApi {

    @Operation(summary = "Create a new task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody TaskRequest request);

    @Operation(summary = "Update an existing task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    ResponseEntity<TaskResponse> update(@Valid @RequestBody TaskRequest request);

    @Operation(summary = "Delete a task by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @Parameter(description = "Task ID")
            @PathVariable UUID id);

    @Operation(summary = "Mark a task as completed")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task completed",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/complete")
    ResponseEntity<TaskResponse> complete(
            @Parameter(description = "Task ID")
            @PathVariable UUID id);

    @Operation(summary = "Start a task (set status to PROCESSING)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task started",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/start")
    ResponseEntity<TaskResponse> startTask(
            @Parameter(description = "Task ID")
            @PathVariable UUID id);

    @Operation(
            summary = "Search tasks with filters and sorting",
            description = "Supports filtering by status, priority, userId, dueDate and one or more tag names. " +
                    "Sorting fields: status, priority, createdAt, dueDate, tagName. " +
                    "Returns tasks WITH their associated tags."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results with tags"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/search")
    ResponseEntity<List<TaskSearchResponse>> search(
            @Valid @RequestBody TaskSearchRequest request);

    @Operation(
            summary = "Get tasks for the next 7 days (weekly view)",
            description = "Returns tasks due within the next 7 days for the given user. " +
                    "Tags are intentionally excluded from this view."
    )
    @ApiResponse(responseCode = "200", description = "Weekly task list (no tags)")
    @GetMapping("/weekly/{userId}")
    ResponseEntity<List<TaskWeeklyResponse>> getWeekly(
            @Parameter(description = "Account (user) ID")
            @PathVariable UUID userId);
}
