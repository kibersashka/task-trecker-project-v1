package com.task.tracker.web.client;

import com.task.tracker.web.config.ServiceProperties;
import com.task.tracker.web.dto.TaskForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class TaskClient {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    private HttpHeaders header(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            httpHeaders.set("Authorization", token);
        }
        return httpHeaders;
    }

    private <T> HttpEntity<T> entity(T body, String token) {
        return new HttpEntity<>(body, header(token));
    }

    private String toInstant(String time) {
        if (time == null || time.isBlank()) {
            return null;
        }
        return time.length() == 16 ? time + ":00Z" : time + "Z";
    }

    public List<Map<String, Object>> search(String token, UUID accountId,
                                            String status, String priority, String tagName) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userId", accountId);
        if (status   != null && !status.isBlank()) {
            body.put("status", status);
        }
        if (priority != null && !priority.isBlank()) {
            body.put("priority", priority);
        }
        if (tagName  != null && !tagName.isBlank()) {
            body.put("tagName",  tagName);
        }

        body.put("sortBy", List.of("dueDate", "priority"));
        try {
            return restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/tasks/search",
                    HttpMethod.POST, entity(body, token),
                    new ParameterizedTypeReference<List<Map<String,Object>>>() {}).getBody();
        } catch (Exception ex) {
            log.error("search: {}", ex.getMessage());
            return List.of();
        }
    }

    public void create(String token, TaskForm f) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title",f.getTitle());
        body.put("description", f.getDescription());
        body.put("status", "CREATED");
        body.put("priority", f.getPriority());
        body.put("accountId", f.getAccountId());
        String due = toInstant(f.getDueDate());

        if (due != null) {
            body.put("dueDate", due);
        }
        if (f.getTagIds() != null && !f.getTagIds().isBlank()) {
            List<String> ids = Arrays.stream(f.getTagIds().split(","))
                    .map(String::trim).filter(s -> !s.isBlank()).toList();
            body.put("tagIds", ids);
        }
        log.info("POST /api/tasks/create: {}", body);
        restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/tasks", HttpMethod.POST, entity(body, token), Void.class);
    }

    public void update(String token, TaskForm f) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", f.getId());
        body.put("title", f.getTitle());
        body.put("description", f.getDescription());
        body.put("status", f.getStatus());
        body.put("priority", f.getPriority());
        body.put("accountId", f.getAccountId());
        String due = toInstant(f.getDueDate());
        if (due != null) {
            body.put("dueDate", due);
        }
        if (f.getTagIds() != null && !f.getTagIds().isBlank()) {
            List<String> ids = Arrays.stream(f.getTagIds().split(","))
                    .map(String::trim).filter(s -> !s.isBlank()).toList();
            body.put("tagIds", ids);
        } else {
            body.put("tagIds", List.of());
        }
        restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/tasks", HttpMethod.PUT, entity(body, token), Void.class);
    }

    public void delete(String token, UUID id) {
        restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/tasks/" + id,
                HttpMethod.DELETE, entity(null, token), Void.class);
    }

    public Map<String, Object> complete(String token, UUID id) {
        return restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/tasks/" + id + "/complete",
                HttpMethod.POST, entity(null, token),
                new ParameterizedTypeReference<Map<String,Object>>() {}).getBody();
    }

    public Map<String, Object> start(String token, UUID id) {
        return restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/tasks/" + id + "/start",
                HttpMethod.POST, entity(null, token),
                new ParameterizedTypeReference<Map<String,Object>>() {}).getBody();
    }


    public List<Map<String, Object>> tags(String token, UUID accountId) {
        try {
            return restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/tags/account/" + accountId,
                    HttpMethod.GET, entity(null, token),
                    new ParameterizedTypeReference<List<Map<String,Object>>>() {}).getBody();
        } catch (Exception ex) {
            log.error("tags: {}", ex.getMessage());
            return List.of();
        }
    }

    public void createTag(String token, UUID accountId, String name, String desc, String color) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name",name);
        body.put("description", desc  != null ? desc  : "");
        body.put("color", color != null ? color : "#94a3b8");
        body.put("accountId", accountId);
        restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/tags", HttpMethod.POST, entity(body, token), Void.class);
    }

    public void updateTag(String token, UUID id, String name, String desc, String color) {
        Map<String, Object> body = Map.of(
                "id", id, "name", name,
                "description",desc  != null ? desc  : "",
                "color",color != null ? color : "#94a3b8"
        );
        restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/tags", HttpMethod.PUT, entity(body, token), Void.class);
    }

    public void deleteTag(String token, UUID id) {
        restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/tags/" + id,
                HttpMethod.DELETE, entity(null, token), Void.class);
    }


    public Map<String, Object> createReminder(String token, UUID taskId, String dateStr) {
        Map<String, Object> body = Map.of("taskId", taskId, "reminderDate", toInstant(dateStr));
        return restTemplate.exchange(serviceProperties.getTaskUrl() + "/api/v1/reminders",
                HttpMethod.POST, entity(body, token),
                new ParameterizedTypeReference<Map<String,Object>>() {}).getBody();
    }
}

