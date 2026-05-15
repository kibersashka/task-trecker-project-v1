package com.task.tracker.taskimpl.mapper;

import com.task.tracker.taskapi.dto.TagRequest;
import com.task.tracker.taskimpl.entity.Tag;
import com.task.tracker.taskapi.dto.TagResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse(Tag tag);

    @Mapping(target = "id", ignore = true)
    Tag toEntity(TagRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    void updateFromRequest(TagRequest request, @MappingTarget Tag tag);
}
