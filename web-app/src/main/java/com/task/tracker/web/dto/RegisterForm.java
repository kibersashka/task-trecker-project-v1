package com.task.tracker.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class RegisterForm {
    @NotBlank @Size(min=3,max=32)
    private String username;
    @NotBlank @Size(min=6)
    private String rawPassword;
    @Email
    private String email;
    private String birthday;
}
