package com.task.tracker.taskapi.api;

import com.task.tracker.commonlib.dto.ErrorResponse;
import com.task.tracker.taskapi.dto.ReminderRequest;
import com.task.tracker.taskapi.dto.ReminderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/reminders")
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "Reminders",
        description = "Reminder management operations"
)
public interface ReminderControllerApi {

    @Operation(summary = "Create a new reminder")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reminder created",
                    content = @Content(schema = @Schema(implementation = ReminderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<ReminderResponse> create(
            @Valid @RequestBody ReminderRequest request);

}
