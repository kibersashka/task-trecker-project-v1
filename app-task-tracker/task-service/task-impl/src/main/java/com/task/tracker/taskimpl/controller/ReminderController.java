package com.task.tracker.taskimpl.controller;

import com.task.tracker.taskapi.api.ReminderControllerApi;
import com.task.tracker.taskapi.dto.ReminderRequest;

import com.task.tracker.taskapi.dto.ReminderResponse;
import com.task.tracker.taskapi.dto.ReminderStatus;
import com.task.tracker.taskimpl.entity.Reminder;
import com.task.tracker.taskimpl.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReminderController implements ReminderControllerApi {

    private final ReminderService reminderService;

    @Override
    public ResponseEntity<ReminderResponse> create(
            @RequestBody ReminderRequest request
    ) {
        ReminderResponse saved = reminderService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}