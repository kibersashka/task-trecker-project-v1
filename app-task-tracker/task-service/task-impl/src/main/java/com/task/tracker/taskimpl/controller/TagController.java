package com.task.tracker.taskimpl.controller;

import com.task.tracker.taskapi.dto.TagResuest;
import com.task.tracker.taskimpl.entity.Tag;
import com.task.tracker.taskimpl.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<Tag> create(@RequestBody TagResuest request) {
        return ResponseEntity.ok(tagService.create(request));
    }

    @PutMapping
    public ResponseEntity<Tag> update(@RequestBody TagResuest request) {
        return ResponseEntity.ok(tagService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Tag>> getByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(tagService.findAllByAccountId(accountId));
    }
}