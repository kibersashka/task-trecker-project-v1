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

@Component @Slf4j @RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    private HttpHeaders header(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", token);
        return httpHeaders;
    }

    public Map<String, Object> info(String token, UUID id) {
        try {
            return restTemplate.exchange(serviceProperties.getUserUrl() + "/api/account-info/" + id,
                    HttpMethod.GET, new HttpEntity<>(header(token)),
                    new ParameterizedTypeReference<Map<String,Object>>() {}).getBody();
        } catch (Exception e) {
            log.error("user info: {}", e.getMessage());
            return Map.of();
        }
    }

    public void updateEmail(String token, UUID id, String email) {
        restTemplate.exchange(serviceProperties.getUserUrl() + "/api/account-info/update/" + id,
                HttpMethod.POST,
                new HttpEntity<>(Map.of("email", email), header(token)),
                Void.class);
    }

    public List<Map<String, Object>> getTop(String token) {
        try {
            return restTemplate.exchange(serviceProperties.getUserUrl() + "/api/account-info/top",
                    HttpMethod.GET, new HttpEntity<>(header(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
        } catch (Exception e) {
            log.error("top users: {}", e.getMessage());
            return List.of();
        }
    }
}
