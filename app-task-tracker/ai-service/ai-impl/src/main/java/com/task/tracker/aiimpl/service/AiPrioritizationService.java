package com.task.tracker.aiimpl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.aiapi.dto.*;
import com.task.tracker.aiimpl.builder.PromptBuilder;
import com.task.tracker.aiimpl.client.AiClient;
import com.task.tracker.aiimpl.entity.AiRequestLog;
import com.task.tracker.aiimpl.entity.AiRequestStatus;
import com.task.tracker.aiimpl.exception.AnthropicApiException;
import com.task.tracker.aiimpl.repository.AiRequestLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiPrioritizationService {

    private final AiClient anthropicClient;
    private final PromptBuilder promptBuilder;
    private final AiRequestLogRepository logRepository;
    private final ObjectMapper objectMapper;


    @Cacheable(value = "ai-prioritization", key = "#request.accountId() + '_' + #request.tasks().hashCode()")
    @Transactional
    public AiPrioritizationResponse prioritize(AiPrioritizationRequest request) {
        log.info("AI prioritization request | accountId={} | taskCount={}",
                request.accountId(), request.tasks().size());

        AiRequestLog logEntry = createLogEntry(request);
        long startTime = System.currentTimeMillis();

        try {
            String systemPrompt = promptBuilder.buildSystemPrompt();
            String userPrompt = promptBuilder.buildUserPrompt(request.tasks());

            AnthropicResponse aiResponse = anthropicClient.complete(systemPrompt, userPrompt);

            long duration = System.currentTimeMillis() - startTime;

            AiPrioritizationResponse result = parseAiResponse(
                    logEntry.getId(),
                    aiResponse.text(),
                    aiResponse.inputTokens(),
                    aiResponse.outputTokens()
            );

            logEntry.setResponsePayload(aiResponse.text());
            logEntry.setStatus(AiRequestStatus.SUCCESS);
            logEntry.setInputTokens(aiResponse.inputTokens());
            logEntry.setOutputTokens(aiResponse.outputTokens());
            logEntry.setDurationMs(duration);
            logRepository.save(logEntry);

            log.info("AI prioritization success | accountId={} | durationMs={} | inputTokens={} | outputTokens={}",
                    request.accountId(), duration, aiResponse.inputTokens(), aiResponse.outputTokens());

            return result;

        } catch (AnthropicApiException e) {
            log.error("Anthropic API error | accountId={} | message={}", request.accountId(), e.getMessage());

            logEntry.setStatus(AiRequestStatus.FAILED);
            logEntry.setErrorMessage(e.getMessage());
            logEntry.setDurationMs(System.currentTimeMillis() - startTime);
            logRepository.save(logEntry);

            throw e;
        }
    }

    @Cacheable(value = "ai-history", key = "#accountId")
    public List<AiRequestLog> getHistory(UUID accountId) {
        return logRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    private AiRequestLog createLogEntry(AiPrioritizationRequest request) {
        String requestJson;
        try {
            requestJson = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            requestJson = "serialization error";
        }

        AiRequestLog log = AiRequestLog.builder()
                .accountId(request.accountId())
                .requestPayload(requestJson)
                .status(AiRequestStatus.PROCESSING)
                .inputTokens(0)
                .outputTokens(0)
                .durationMs(0)
                .build();

        return logRepository.save(log);
    }

    private AiPrioritizationResponse parseAiResponse(
            UUID requestId, String rawText, int inputTokens, int outputTokens) {
        try {
            String cleanText = rawText
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            JsonNode root = objectMapper.readTree(cleanText);

            List<PrioritizedTask> recommendations = new ArrayList<>();
            JsonNode recArray = root.path("recommendations");

            for (JsonNode node : recArray) {
                recommendations.add(new PrioritizedTask(
                        UUID.fromString(node.path("taskId").asText()),
                        node.path("recommendedOrder").asInt(),
                        node.path("suggestedPriority").asText("MIDDLE"),
                        node.path("reason").asText()
                ));
            }

            String generalAdvice = root.path("generalAdvice").asText("");

            return new AiPrioritizationResponse(requestId, recommendations, generalAdvice, inputTokens, outputTokens);

        } catch (Exception e) {
            log.error("Не удалось распарсить JSON от Claude | text={}", rawText);
            throw new AnthropicApiException("Claude вернул некорректный JSON: " + e.getMessage());
        }
    }
}