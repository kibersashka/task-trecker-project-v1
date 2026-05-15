package com.task.tracker.taskimpl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.taskapi.dto.TagRequest;
import com.task.tracker.taskapi.dto.TagResponse;
import com.task.tracker.taskimpl.entity.Tag;
import com.task.tracker.taskimpl.exception.TagExistsException;
import com.task.tracker.taskimpl.exception.TagNotFoundException;
import com.task.tracker.taskimpl.mapper.TagMapper;
import com.task.tracker.taskimpl.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final ObjectMapper objectMapper;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagResponse create(TagRequest tag) {
        if (tag.name() == null) {
            throw new TagNotFoundException(null);
        }
        if (tag.id() != null && tagRepository.existsById(tag.id())) {
            throw new TagExistsException(tag.id());
        }

        if (tagRepository.existsByNameAndDescription((tag.name()), tag.description())) {
            throw new TagExistsException(null);
        }
        Tag tagEntity = tagMapper.toEntity(tag);
        return tagMapper.toResponse(tagRepository.save(tagEntity));
    }

    public TagResponse update(TagRequest tag) {
        Tag tagEntity = tagRepository.findById(tag.id())
                .orElseThrow(() -> new TagNotFoundException(tag.id()));
        tagMapper.updateFromRequest(tag, tagEntity);
        return tagMapper.toResponse(tagRepository.save(tagEntity));
    }

    public void delete(UUID id) {
        tagRepository.deleteById(id);
    }

    public List<TagResponse> findAllByAccountId(UUID accountId) {
        log.debug("Finding all tags by accountId {}", accountId);
        return tagRepository.findByAccountId(accountId).stream()
                .map(tagMapper::toResponse)
                .toList();
    }
}
