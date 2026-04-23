package com.task.tracker.authimpl.service;

import com.task.tracker.authapi.dto.*;
import com.task.tracker.authapi.status.Role;
import com.task.tracker.authimpl.entity.Account;
import com.task.tracker.authimpl.jwt.service.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtService jwtService;
    private final AccountService accountService;
    private final AuthenticatedService authenticatedService;

    @Transactional
    public TokenCouple login(String username, String password) {
        Account account = authenticatedService.authenticate(username, password);

        accountService.isActive(account);

        TokenCouple couple = jwtService.generateTokenCouple(account);

        log.info("Account login: {}", account.getId());

        return couple;
    }

    @Transactional
    public TokenCouple refresh(Account account) {
        return jwtService.generateTokenCouple(account);
    }

    @Transactional
    public SignInResponse signIn(SignInRequest registerRequest) {
        Account account = accountService.save(registerRequest);

        log.info("Account registered | accountId={}", account.getId());

        Set<Role> roles = account.getRoles().stream()
                .map(r -> Role.valueOf(r.name()))
                .collect(Collectors.toSet());

        return new SignInResponse(
                account.getId(),
                account.getUsername(),
                roles
        );
    }


    public void logout(String requestRefreshToken) {
        jwtService.invalidateRefreshToken(requestRefreshToken);
    }

    @Transactional
    public UpdateResponse updateRole(UpdateRequest updateRequest) {
        Account account = accountService.updateAddRole(updateRequest.id(), updateRequest.role().name());

        log.info("Account registered | accountId={}", account.getId());

        Set<Role> roles = account.getRoles().stream()
                .map(r -> Role.valueOf(r.name()))
                .collect(Collectors.toSet());

        return new UpdateResponse(
                account.getId(),
                account.getUsername(),
                roles
        );
    }
    //TODO удалить роль
}
