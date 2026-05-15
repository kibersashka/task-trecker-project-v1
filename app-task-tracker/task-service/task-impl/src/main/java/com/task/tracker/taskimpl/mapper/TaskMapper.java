package com.task.tracker.taskimpl.mapper;

import com.task.tracker.taskapi.dto.TaskRequest;
import com.task.tracker.taskapi.dto.TaskResponse;
import com.task.tracker.taskapi.dto.TaskSearchResponse;
import com.task.tracker.taskapi.dto.TaskWeeklyResponse;
import com.task.tracker.taskimpl.entity.Tag;
import com.task.tracker.taskimpl.entity.Task;
import org.mapstruct.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = TagMapper.class)
public interface TaskMapper {

    TaskSearchResponse toResponse(Task task);

    @Mapping(
            target = "tagIds",
            source = "tags",
            qualifiedByName = "mapTagsToIds"
    )
    TaskResponse toTaskResponse(Task task);

    @Named("mapTagsToIds")
    default Set<UUID> mapTagsToIds(Set<Tag> tags) {

        if (tags == null) {
            return Set.of();
        }

        return tags.stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateTaskFromRequest(TaskRequest request,
                               @MappingTarget Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Task toEntity(TaskRequest request);

    @Mapping(source = "accountId", target = "accountId")
    TaskWeeklyResponse toWeeklyResponse(Task task);
}