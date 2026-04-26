package com.task.tracker.userimpl.service;

import com.task.tracker.commonlib.dto.AccountUpdateRequest;
import com.task.tracker.commonlib.dto.AccountUpdateResponse;
import com.task.tracker.userimpl.entity.AccountInfo;
import com.task.tracker.userimpl.exception.UserNotFoundException;
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

    public AccountInfo findAccountInfoById(UUID id) {
        return accountInfoRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );
    }

    public List<AccountInfo> findUsersAboveAverageXp() {
        return accountInfoRepository.findUsersAboveAverageXp();
    }

    @Transactional
    public void updateXp(AccountInfo accountInfo, Integer xp) {
        Integer currentXp = accountInfo.getXp();
        accountInfo.setXp(currentXp + xp);
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
