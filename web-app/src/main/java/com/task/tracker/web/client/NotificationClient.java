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
public class NotificationClient {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    private HttpHeaders header(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", token);
        return httpHeaders;
    }

    public List<Map<String, Object>> findAll(String token, UUID accountId) {
        try {
            List<Map<String, Object>> res = restTemplate.exchange(
                    serviceProperties.getNotificationUrl() + "/api/notifications/" + accountId,
                    HttpMethod.GET, new HttpEntity<>(header(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
            log.info("res={}", res);
            return res;
        } catch (Exception e) {
            log.error("notifications: {}", e.getMessage());
            return List.of();
        }
    }

    public void markAsRead(String token, UUID accountId, UUID notificationId) {
        restTemplate.exchange(
                serviceProperties.getNotificationUrl() + "/api/notifications/send/" + accountId + "/" + notificationId,
                HttpMethod.GET, new HttpEntity<>(header(token)),
                Void.class);
    }
}
