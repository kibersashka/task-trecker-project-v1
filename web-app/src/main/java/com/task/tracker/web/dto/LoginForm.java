package com.task.tracker.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginForm {
    @NotBlank(message = "Введите имя пользователя")
    private String username;
    @NotBlank(message = "Введите пароль")
    private String password;
}
