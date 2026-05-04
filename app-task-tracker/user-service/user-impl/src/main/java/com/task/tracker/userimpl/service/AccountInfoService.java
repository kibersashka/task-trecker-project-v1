package com.task.tracker.userimpl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tracker.commonlib.dto.AccountLevelUpEvent;
import com.task.tracker.commonlib.dto.AccountUpdateRequest;
import com.task.tracker.commonlib.dto.AccountUpdateResponse;
import com.task.tracker.commonlib.dto.AccountStatus;
import com.task.tracker.userimpl.entity.AccountInfo;
import com.task.tracker.userimpl.exception.UserNotFoundException;
import com.task.tracker.userimpl.kafka.port.EventPublisher;
import com.task.tracker.userimpl.mapper.AccountInfoMapper;
import com.task.tracker.userimpl.repository.AccountInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountInfoService {
    private final AccountInfoRepository accountInfoRepository;
    private final AccountInfoMapper accountInfoMapper;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public AccountInfo findAccountInfoById(UUID id) {
        return accountInfoRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );
    }

    public List<AccountInfo> findUsersAboveAverageXp() {
        return accountInfoRepository.findUsersAboveAverageXp();
    }

    @Transactional
    public void updateXp(UUID id, Integer xp) {
        AccountInfo accountInfo = accountInfoRepository.findAccountInfoById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id)
                );

        log.info("Xp updated: [{}]", accountInfo);
        Integer currentXp = accountInfo.getXp();
        accountInfo.setXp(currentXp + xp);

        AccountStatus oldStatus = accountInfo.getAccountStatus();

        while (canLevelUp(accountInfo)) {
            levelUp(accountInfo);
        }

        AccountStatus newStatus = accountInfo.getAccountStatus();

        if (!oldStatus.equals(newStatus)) {
            AccountLevelUpEvent accountLevelUpEvent = new AccountLevelUpEvent(
                    accountInfo.getEmail(),
                    newStatus
            );

            try {
                eventPublisher.publishLevelUp(
                        objectMapper.writeValueAsString(accountLevelUpEvent),
                        accountInfo.getId()
                );
            } catch (JsonProcessingException e) {
                log.error("Failed to publish level up event", e);
                throw new RuntimeException(e);
            }
        }
    }

    private boolean canLevelUp(AccountInfo accountInfo) {
        return switch (accountInfo.getAccountStatus()) {
            case INTER -> accountInfo.getXp() >= AccountStatus.JUNIOR.getXpCount();
            case JUNIOR -> accountInfo.getXp() >= AccountStatus.MIDDLE.getXpCount();
            case MIDDLE -> accountInfo.getXp() >= AccountStatus.SENIOR.getXpCount();
            case SENIOR -> false;
        };
    }

    private void levelUp(AccountInfo accountInfo) {
        switch (accountInfo.getAccountStatus()) {
            case INTER -> accountInfo.setAccountStatus(AccountStatus.JUNIOR);
            case JUNIOR -> accountInfo.setAccountStatus(AccountStatus.MIDDLE);
            case MIDDLE -> accountInfo.setAccountStatus(AccountStatus.SENIOR);
            case SENIOR -> {}
        }
    }

    public void save(AccountInfo accountInfo) {
        accountInfoRepository.save(accountInfo);
    }

    @Transactional
    public AccountUpdateResponse update(UUID id, AccountUpdateRequest accountInfoRequest) {
        AccountInfo accountInfo =  accountInfoRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );

        if (accountInfoRequest.email() != null) {
            accountInfo.setEmail(accountInfoRequest.email());
        }

        return accountInfoMapper.toResponse(accountInfo);
    }
}
