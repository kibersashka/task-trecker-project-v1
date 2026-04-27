package com.task.tracker.taskimpl.mapper;

import com.task.tracker.taskapi.dto.TagResuest;
import com.task.tracker.taskimpl.entity.Tag;
import com.task.tracker.taskapi.dto.TagResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse(Tag tag);

    @Mapping(target = "id", ignore = true)
    Tag toEntity(TagResuest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateFromRequest(TagResuest request, @MappingTarget Tag tag);
}
