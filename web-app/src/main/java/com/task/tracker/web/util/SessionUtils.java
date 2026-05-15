package com.task.tracker.web.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.web.service.UserSession;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionUtils {

    private static final String KEY = "US";
    private final ObjectMapper mapper;

    public UserSession get(HttpSession httpSession) {
        return (UserSession) httpSession.getAttribute(KEY);
    }

    public boolean ok(HttpSession httpSession) {
        UserSession userSession = get(httpSession);
        return userSession != null && userSession.loggedIn();
    }

    public String extractRole(String token) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(token.split("\\.")[1]);
            JsonNode claims = mapper.readTree(bytes);

            JsonNode roles = claims.path("roles");
            if (roles.isArray() && roles.size() > 0) {
                String raw = roles.get(0).asText();
                return raw.replace("ROLE_", "");
            }
            return "USER";
        } catch (Exception e) {
            return "USER";
        }
    }
    public void save(HttpSession httpSession, UserSession userSession) {
        httpSession.setAttribute(KEY, userSession);
    }

    public void clear(HttpSession httpSession) {
        httpSession.invalidate();
    }

    public UUID extractId(String token) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(token.split("\\.")[1]);
            return UUID.fromString(mapper.readTree(bytes).path("id").asText());
        } catch (Exception e) {
            throw new RuntimeException("Не удалось прочитать токен");
        }
    }

    public String extractUsername(String token) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(token.split("\\.")[1]);
            return mapper.readTree(bytes).path("sub").asText("?");
        } catch (Exception e) {
            return "?";
        }
    }
}