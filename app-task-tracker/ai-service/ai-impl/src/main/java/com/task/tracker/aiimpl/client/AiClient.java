package com.task.tracker.aiimpl.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.task.tracker.aiapi.dto.AnthropicResponse;
import com.task.tracker.aiimpl.config.properties.GroqProperties;
import com.task.tracker.aiimpl.exception.AnthropicApiException;
import com.task.tracker.aiimpl.exception.RateLimitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AiClient {

    private static final String ENDPOINT = "/openai/v1/chat/completions";
    private static final MediaType JSON   = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient   httpClient;
    private final ObjectMapper   objectMapper;
    private final GroqProperties properties;


    public AnthropicResponse complete(String systemPrompt, String userPrompt) {
        String url         = properties.getBaseUrl() + ENDPOINT;
        String requestBody = buildRequestBody(systemPrompt, userPrompt);

        log.debug("Groq request | model={} | url={}", properties.getModel(), url);

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + properties.getApiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, JSON))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                log.error("Groq error | status={} | body={}", response.code(), body);
                throw new AnthropicApiException(
                        "Groq вернул " + response.code() + ": " + body
                );
            }

            if (response.code() == 429) {
                log.warn("Rate limit hit, retry later");
                throw new RateLimitException("Groq rate limit");
            }

            log.debug("Groq response: {}", body);
            return parseResponse(body);

        } catch (IOException e) {
            log.error("Groq connection error: {}", e.getMessage());
            throw new AnthropicApiException("Нет соединения с Groq: " + e.getMessage());
        }
    }

    /**
     * {
     *   "model": "llama-3.3-70b-versatile",
     *   "messages": [
     *     { "role": "system",  "content": "..." },
     *     { "role": "user",    "content": "..." }
     *   ],
     *   "max_tokens": 1024,
     *   "temperature": 0.3
     * }
     */
    private String buildRequestBody(String systemPrompt, String userPrompt) {
        try {
            ObjectNode root = objectMapper.createObjectNode();

            root.put("model", properties.getModel());
            root.put("max_tokens", properties.getMaxTokens());
            root.put("temperature", 0.3);

            ArrayNode messages = root.putArray("messages");

            ObjectNode system = messages.addObject();
            system.put("role", "system");
            system.put("content", systemPrompt != null ? systemPrompt : "");

            ObjectNode user = messages.addObject();
            user.put("role", "user");
            user.put("content", userPrompt != null ? userPrompt : "");

            return objectMapper.writeValueAsString(root);

        } catch (Exception e) {
            throw new AnthropicApiException("Ошибка сборки запроса к Groq: " + e.getMessage());
        }
    }

    /**
     *
     * {
     *   "choices": [{ "message": { "content": "..." } }],
     *   "usage": { "prompt_tokens": N, "completion_tokens": M }
     * }
     */
    private AnthropicResponse parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            String text = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            int inputTokens  = root.path("usage").path("prompt_tokens").asInt(0);
            int outputTokens = root.path("usage").path("completion_tokens").asInt(0);

            log.info("Groq OK | model={} | in={} out={}",
                    properties.getModel(), inputTokens, outputTokens);

            return new AnthropicResponse(text, inputTokens, outputTokens);

        } catch (Exception e) {
            log.error("Не удалось распарсить ответ Groq | body={}", responseBody);
            throw new AnthropicApiException("Некорректный ответ от Groq: " + e.getMessage());
        }
    }
}