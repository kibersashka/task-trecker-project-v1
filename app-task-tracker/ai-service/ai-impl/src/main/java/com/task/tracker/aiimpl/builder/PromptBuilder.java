package com.task.tracker.aiimpl.builder;

import com.task.tracker.aiapi.dto.TaskSummary;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
public class PromptBuilder {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.of("UTC"));

    private static final String SYSTEM_PROMPT = """
            Ты — персональный ассистент по управлению задачами. 
            Твоя цель — помочь пользователю расставить приоритеты задач на основе их важности, срочности и дедлайнов.
            
            Правила ответа:
            1. Отвечай строго в формате JSON — никакого лишнего текста до или после JSON.
            2. JSON должен содержать поля: "recommendations" (массив) и "generalAdvice" (строка).
            3. Каждый элемент "recommendations": {"taskId": "uuid", "recommendedOrder": число, "suggestedPriority": "LOW/MIDDLE/HIGH", "reason": "причина на русском"}.
            4. recommendedOrder начинается с 1 — чем меньше, тем раньше нужно сделать.
            5. Отвечай на русском языке.
            """;

    public String buildSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    public String buildUserPrompt(List<TaskSummary> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("Вот мои задачи на сегодня. Расставь приоритеты:\n\n");

        for (int i = 0; i < tasks.size(); i++) {
            TaskSummary task = tasks.get(i);
            sb.append("%d. ID: %s\n".formatted(i + 1, task.id()));
            sb.append("   Название: %s\n".formatted(task.title()));

            if (task.description() != null && !task.description().isBlank()) {
                sb.append("   Описание: %s\n".formatted(task.description()));
            }

            sb.append("   Приоритет: %s\n".formatted(task.priority()));
            sb.append("   Статус: %s\n".formatted(task.status()));

            if (task.dueDate() != null) {
                sb.append("   Дедлайн: %s UTC\n".formatted(FORMATTER.format(task.dueDate())));
            } else {
                sb.append("   Дедлайн: не указан\n");
            }
            sb.append("\n");
        }

        sb.append("Верни JSON с полями recommendations и generalAdvice.");
        return sb.toString();
    }
}