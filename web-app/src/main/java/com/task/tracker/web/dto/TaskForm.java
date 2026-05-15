package com.task.tracker.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TaskForm {
    private UUID id;
    @NotBlank(message = "Название не может быть пустым")
    private String title;
    private String description;
    private String status = "CREATED";
    private String priority = "MIDDLE";
    private String dueDate;
    private UUID   accountId;
    private String tagIds;
}
