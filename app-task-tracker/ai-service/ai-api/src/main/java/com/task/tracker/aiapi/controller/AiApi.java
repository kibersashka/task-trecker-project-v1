package com.task.tracker.aiapi.controller;

import com.task.tracker.aiapi.dto.AiPrioritizationRequest;
import com.task.tracker.aiapi.dto.AiPrioritizationResponse;
import com.task.tracker.aiapi.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "AI Prioritization", description = "AI-анализ и расстановка приоритетов задач")
@RequestMapping("/api/ai")
public interface AiApi {

    @Operation(
            summary = "Расставить приоритеты задач",
            description = "Отправляет список задач в Anthropic Claude и возвращает рекомендации по порядку выполнения"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Рекомендации успешно получены",
                    content = @Content(schema = @Schema(implementation = AiPrioritizationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный запрос",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Anthropic API недоступен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/prioritize")
    ResponseEntity<AiPrioritizationResponse> prioritize(
            @Valid @RequestBody AiPrioritizationRequest request
    );
}