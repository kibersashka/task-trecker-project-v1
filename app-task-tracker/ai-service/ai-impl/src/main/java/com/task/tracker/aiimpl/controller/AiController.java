package com.task.tracker.aiimpl.controller;

import com.task.tracker.aiapi.controller.AiApi;
import com.task.tracker.aiapi.dto.AiPrioritizationRequest;
import com.task.tracker.aiapi.dto.AiPrioritizationResponse;
import com.task.tracker.aiimpl.service.AiPrioritizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AiController implements AiApi {

    private final AiPrioritizationService prioritizationService;

    @Override
    public ResponseEntity<AiPrioritizationResponse> prioritize(AiPrioritizationRequest request) {
        log.info("POST /api/ai/prioritize | accountId={}", request.accountId());
        return ResponseEntity.ok(prioritizationService.prioritize(request));
    }
}