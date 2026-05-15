package com.task.tracker.web.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.task.tracker.web.config.ServiceProperties;
import com.task.tracker.web.dto.LoginForm;
import com.task.tracker.web.dto.RegisterForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Component @Slf4j @RequiredArgsConstructor
public class AuthClient {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    public String login(LoginForm loginForm) {
        String url = UriComponentsBuilder
                .fromHttpUrl(serviceProperties.getAuthUrl() + "/auth/login")
                .queryParam("username", loginForm.getUsername())
                .queryParam("password", loginForm.getPassword())
                .toUriString();
        try {
            JsonNode body = restTemplate.getForEntity(url, JsonNode.class).getBody();
            String token = body != null ? body.path("accessToken").asText() : null;
            if (token == null || token.isBlank()) {
                throw new RuntimeException("Сервер не вернул токен");
            }
            return token;
        } catch (HttpClientErrorException e) {
            log.warn("Login failed {}", e.getStatusCode());
            throw new RuntimeException("Неверное имя пользователя или пароль");
        }
    }

    public void register(RegisterForm registerForm) {
        Map<String, Object> body = new HashMap<>();
        body.put("username", registerForm.getUsername());
        body.put("rawPassword", registerForm.getRawPassword());
        body.put("email", registerForm.getEmail() != null ? registerForm.getEmail() : "");
        body.put("role", "USER");
        if (registerForm.getBirthday() != null && !registerForm.getBirthday().isBlank()) {
            body.put("birthday", registerForm.getBirthday() + "T00:00:00Z");
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            restTemplate.postForEntity(serviceProperties.getAuthUrl() + "/auth/register",
                    new HttpEntity<>(body, httpHeaders), JsonNode.class);
        } catch (HttpClientErrorException e) {
            log.warn("Register failed {}", e.getStatusCode());
            throw new RuntimeException("Пользователь уже существует");
        }
    }
}
