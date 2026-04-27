package com.task.tracker.taskimpl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.taskapi.dto.TagResuest;
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

    public Tag create(TagResuest tag) {
        if (tag.name() == null) {
            throw new TagNotFoundException(null);
        }
        if (tagRepository.existsById(tag.id())) {
            throw new TagExistsException(tag.id());
        }
        Tag tagEntity = tagMapper.toEntity(tag);
        return tagRepository.save(tagEntity);
    }

    public Tag update(TagResuest tag) {
        Tag tagEntity = tagRepository.findById(tag.id())
                .orElseThrow(() -> new TagNotFoundException(tag.id()));
        tagMapper.updateFromRequest(tag, tagEntity);
        return tagRepository.save(tagEntity);
    }

    public void delete(UUID id) {
        tagRepository.deleteById(id);
    }

    public List<Tag> findAllByAccountId(UUID accountId) {
        return tagRepository.findTagsByAccountId(accountId);
    }
}
