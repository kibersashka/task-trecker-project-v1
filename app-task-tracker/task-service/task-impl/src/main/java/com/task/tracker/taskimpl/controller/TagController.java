package com.task.tracker.taskimpl.controller;

import com.task.tracker.taskapi.api.TagControllerApi;
import com.task.tracker.taskapi.dto.TagRequest;
import com.task.tracker.taskapi.dto.TagResponse;
import com.task.tracker.taskimpl.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TagController implements TagControllerApi {

    private final TagService tagService;

    @Override
    public ResponseEntity<TagResponse> create(TagRequest request) {
        return ResponseEntity.ok(tagService.create(request));
    }

    @Override
    public ResponseEntity<TagResponse> update(TagRequest request) {
        return ResponseEntity.ok(tagService.update(request));
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<TagResponse>> getByAccount(UUID accountId) {
        return ResponseEntity.ok(tagService.findAllByAccountId(accountId));
    }
}