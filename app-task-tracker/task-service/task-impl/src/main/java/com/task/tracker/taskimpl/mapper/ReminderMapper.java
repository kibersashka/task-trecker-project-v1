package com.task.tracker.taskimpl.mapper;

import com.task.tracker.taskapi.dto.ReminderResponse;
import com.task.tracker.taskimpl.entity.Reminder;
import com.task.tracker.taskimpl.entity.Task;
import com.task.tracker.taskapi.dto.ReminderRequest;
import com.task.tracker.taskapi.dto.ReminderStatus;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReminderMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", source = "taskId", qualifiedByName = "mapTask")
    @Mapping(target = "reminderTime", source = "reminderDate")
    @Mapping(target = "status", constant = "READY_SEND")
    Reminder toEntity(ReminderRequest request);


    @Named("mapTask")
    default Task mapTask(UUID taskId) {
        if (taskId == null) return null;

        Task task = new Task();
        task.setId(taskId);
        return task;
    }

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "reminderId", source = "id")
    @Mapping(target = "reminderDate", source = "reminderTime")
    ReminderResponse toResponse(Reminder reminder);

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "reminderDate", source = "reminderTime")
    ReminderRequest toDto(Reminder reminder);
}