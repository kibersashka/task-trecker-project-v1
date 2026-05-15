package com.task.tracker.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TagForm {
    private UUID id;
    @NotBlank(message = "Введите название тега")
    private String name;
    private String description;
    private String color = "#94a3b8";
}
