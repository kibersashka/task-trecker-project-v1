package com.task.tracker.taskapi.api;
import com.task.tracker.commonlib.dto.ErrorResponse;
import com.task.tracker.taskapi.dto.TagRequest;
import com.task.tracker.taskapi.dto.TagResponse;
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

@RequestMapping("/api/tags")
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "Tags",
        description = "Tag management operations"
)
public interface TagControllerApi {

    @Operation(summary = "Create a new tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag created",
                    content = @Content(schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Tag already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<TagResponse> create(
            @Valid @RequestBody TagRequest request);

    @Operation(summary = "Update an existing tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag updated",
                    content = @Content(schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    ResponseEntity<TagResponse> update(
            @Valid @RequestBody TagRequest request);

    @Operation(summary = "Delete a tag by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag deleted"),
            @ApiResponse(responseCode = "404", description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @Parameter(description = "Tag ID")
            @PathVariable UUID id);

    @Operation(summary = "Get all tags for an account")
    @ApiResponse(responseCode = "200", description = "List of tags")
    @GetMapping("/account/{accountId}")
    ResponseEntity<List<TagResponse>> getByAccount(
            @Parameter(description = "Account (user) ID")
            @PathVariable UUID accountId);
}
