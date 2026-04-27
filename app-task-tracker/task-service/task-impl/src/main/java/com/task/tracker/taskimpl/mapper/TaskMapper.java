package com.task.tracker.taskimpl.mapper;

import com.task.tracker.taskapi.dto.TaskRequest;
import com.task.tracker.taskimpl.entity.Task;
import com.task.tracker.taskapi.dto.TaskSearchResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = TagMapper.class)
public interface TaskMapper {

    @Mapping(source = "accountId", target = "accountId")
    TaskSearchResponse toResponse(Task task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateTaskFromRequest(TaskRequest request, @MappingTarget Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Task toEntity(TaskRequest request);
}