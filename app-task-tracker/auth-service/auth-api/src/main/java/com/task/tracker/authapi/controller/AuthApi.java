package com.task.tracker.authapi.controller;

import com.task.tracker.authapi.dto.LoginResponse;
import com.task.tracker.authapi.dto.SignUpRequest;
import com.task.tracker.authapi.dto.SignUpResponse;
import com.task.tracker.authapi.dto.ErrorResponse;
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

@Tag(name = "Authentication", description = "API аутентификации пользователей")
@RequestMapping("/auth")
public interface AuthApi {

    @Operation(
            summary = "Авторизация пользователя",
            description = "Возвращает access token и устанавливает refresh token в cookie"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная авторизация",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверный логин или пароль",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/login")
    ResponseEntity<LoginResponse> login(

            @Parameter(
                    description = "Имя пользователя",
                    example = "admin",
                    required = true
            )
            @RequestParam
            String username,

            @Parameter(
                    description = "Пароль пользователя",
                    example = "password123",
                    required = true
            )
            @RequestParam
            String password
    );


    @Operation(
            summary = "Обновление access token",
            description = "Обновляет access token по refresh token из cookie"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token успешно обновлён",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token отсутствует или недействителен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/refresh")
    ResponseEntity<LoginResponse> refresh(

            @Parameter(
                    description = "Refresh token из cookie",
                    required = true
            )
            @CookieValue("refreshToken")
            String refreshToken
    );


    @Operation(
            summary = "Регистрация пользователя",
            description = "Создаёт новый аккаунт пользователя"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = SignUpResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации входных данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/register")
    ResponseEntity<SignUpResponse> register(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные регистрации пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignUpRequest.class))
            )
            @Valid
            @RequestBody
            SignUpRequest request
    );
}
