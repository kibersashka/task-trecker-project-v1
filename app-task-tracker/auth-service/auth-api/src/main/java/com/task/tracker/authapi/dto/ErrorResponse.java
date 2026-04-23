package com.task.tracker.authapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ответ с описанием ошибки")
public record ErrorResponse(

        @Schema(description = "HTTP статус код")
        int status,

        @Schema(description = "Краткий код ошибки словами")
        String error,

        @Schema(description = "Сообщение")
        String message,

        @Schema(description = "Путь запроса")
        String path,

        @Schema(description = "Время возникновения ошибки")
        Instant timestamp,

        @Schema(description = "Детали ошибок валидации (только для 400)")
        Object details
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now(), null);
    }
}
