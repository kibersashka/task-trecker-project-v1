package com.task.tracker.userimpl.controller;

import com.task.tracker.commonlib.dto.AccountUpdateRequest;
import com.task.tracker.commonlib.dto.AccountUpdateResponse;
import com.task.tracker.userimpl.entity.AccountInfo;
import com.task.tracker.userimpl.service.AccountInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/account-info")
@Slf4j
@RequiredArgsConstructor
public class AccountInfoController {
    private final AccountInfoService accountInfoService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountInfo> getAccountInfo(@PathVariable UUID id) {
        return ResponseEntity.ok(accountInfoService.findAccountInfoById(id));
    }

    @GetMapping("/top")
    //поменять
    public ResponseEntity<List<AccountInfo>> getTopAccountInfo() {
        return ResponseEntity.ok(accountInfoService.findUsersAboveAverageXp());
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<AccountUpdateResponse> updateAccountInfo(
            @PathVariable UUID id,
            @RequestBody AccountUpdateRequest accountInfo
    ) {
        return ResponseEntity.ok(accountInfoService.update(id, accountInfo));
    }
}
