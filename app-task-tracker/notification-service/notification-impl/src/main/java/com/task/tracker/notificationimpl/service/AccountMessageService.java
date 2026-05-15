package com.task.tracker.notificationimpl.service;

import com.task.tracker.commonlib.dto.AccountLevelUpEvent;
import com.task.tracker.commonlib.dto.AccountStatus;
import com.task.tracker.notificationapi.dto.MessageStatus;
import com.task.tracker.notificationimpl.emailClient.EmailClient;
import com.task.tracker.notificationimpl.entity.AccountMessage;
import com.task.tracker.notificationimpl.entity.Notification;
import com.task.tracker.notificationimpl.exception.AccountMessageNotFoundException;
import com.task.tracker.notificationimpl.repository.AccountMessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.security.auth.login.AccountNotFoundException;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountMessageService {
    private final AccountMessageRepository repository;
    private final EmailClient emailClient;

    @Transactional
    public UUID saveForEmailNotification(AccountLevelUpEvent command) {
        AccountMessage notification = AccountMessage.builder()
                .email(command.email())
                .status(MessageStatus.AWAITING_DISPATCH)
                .accountStatus(command.accountStatus())
                .build();
        return repository.save(notification).getId();
    }

    @Transactional
    public void makeAsSend(UUID accountId) {
        AccountMessage accountMessage = repository.findById(accountId).orElseThrow(
                () -> new AccountMessageNotFoundException(accountId)
        );
        accountMessage.setStatus(MessageStatus.SENT);
    }
}
