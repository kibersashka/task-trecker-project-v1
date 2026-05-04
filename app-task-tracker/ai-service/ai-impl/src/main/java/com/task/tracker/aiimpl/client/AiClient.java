package com.task.tracker.aiimpl.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.aiapi.dto.AnthropicResponse;
import com.task.tracker.aiimpl.config.properties.GeminiProperties;
import com.task.tracker.aiimpl.exception.AnthropicApiException;
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

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final GeminiProperties properties;

    public AnthropicResponse complete(String systemPrompt, String userPrompt) {
        String requestBody = buildRequestBody(systemPrompt, userPrompt);

        Request request = new Request.Builder()
                .url(properties.getBaseUrl()
                        + "/v1/"
                        + properties.getModel()
                        + ":generateContent?key="
                        + properties.getApiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "empty body";

                log.error("AnthropicClient request failed with status code {}", response.code());

                throw new AnthropicApiException(
                        "AnthropicClient request failed with status code " + response.code()
                );
            }

            String responseBody = response.body().string();

            return parseResponse(responseBody);

        } catch (IOException e) {
            log.error("Error for Anthropic API | message={}", e.getMessage());
            throw new AnthropicApiException("Not connect for Anthropic API: " + e.getMessage());        }
    }

    private String buildRequestBody(String systemPrompt, String userPrompt) {
        try {
            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", systemPrompt + "\n\n" + userPrompt)
                                    )
                            )
                    )
            );
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new AnthropicApiException("Nor Serialize Anthropic");
        }
    }

    private AnthropicResponse parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            String text = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            int inputTokens = root.path("usageMetadata").path("promptTokenCount").asInt(0);
            int outputTokens = root.path("usageMetadata").path("candidatesTokenCount").asInt(0);

            log.debug("Anthropic response received | inputTokens={} | outputTokens={}", inputTokens, outputTokens);

            return new AnthropicResponse(text, inputTokens, outputTokens);

        } catch (Exception e) {
            log.error("Error for Anthropic | body={}", responseBody);
            throw new AnthropicApiException("Nor valid Anthropic API");
        }
    }
}
