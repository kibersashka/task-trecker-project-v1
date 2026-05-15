package com.task.tracker.web.client;

import com.task.tracker.web.config.ServiceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AiClient {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    public Map<String, Object> prioritize(UUID accountId, List<Map<String, Object>> raw) {
        List<Map<String, Object>> tasks = raw.stream()
                .filter(t -> !"COMPLETED".equals(t.get("status")))
                .map(t -> Map.<String, Object>of(
                        "id",          String.valueOf(t.getOrDefault("id", UUID.randomUUID())),
                        "title",       String.valueOf(t.getOrDefault("title", "")),
                        "description", String.valueOf(t.getOrDefault("description", "")),
                        "priority",    String.valueOf(t.getOrDefault("priority", "MIDDLE")),
                        "status",      String.valueOf(t.getOrDefault("status",   "CREATED"))
                )).toList();

        Map<String, Object> body = Map.of("accountId", accountId, "tasks", tasks);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.exchange(serviceProperties.getAiUrl() + "/api/ai/prioritize",
                HttpMethod.POST, new HttpEntity<>(body, httpHeaders),
                new ParameterizedTypeReference<Map<String,Object>>() {}).getBody();
    }
}
