package com.task.tracker.authimpl.service;

import com.task.tracker.authapi.dto.SignInRequest;
import com.task.tracker.authapi.status.AccountStatus;
import com.task.tracker.authapi.status.Role;
import com.task.tracker.authimpl.entity.Account;
import com.task.tracker.authimpl.exception.AccountNotActiveException;
import com.task.tracker.authimpl.exception.AccountNotFoundException;
import com.task.tracker.authimpl.exception.NoSuchDefaultAccountRoleException;
import com.task.tracker.authimpl.exception.UsernameAlreadyExistsException;
import com.task.tracker.authimpl.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account findAccountById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Account save(SignInRequest requestDto) {
        String username = requestDto.username();
        String rawPassword = requestDto.rawPassword();

        if (accountRepository.existsByUsername((username))) {
            log.warn("Username {} already exists", username);
            throw new UsernameAlreadyExistsException(username);
        }

        Account account = new Account();
        account.setUsername(username);
        account.setRoles(Set.of(
                        Role.USER
                )
        );
        account.setStatus(AccountStatus.ACTIVE);
        account.setHashedPassword(passwordEncoder.encode(rawPassword));

        return accountRepository.save(account);

    }

    public Account updateStatus(UUID id, boolean enabled) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
        if(enabled) {
            account.setStatus(AccountStatus.ACTIVE);
        } else {
            account.setStatus(AccountStatus.SUSPENDED);
        }
        return accountRepository.save(account);
    }

    public Account updateAddRole(UUID id, String roleName) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));

        account.getRoles().add(Role.valueOf(roleName));

        return accountRepository.save(account);
    }

    public Account updateRemoveRole(UUID id, String roleName) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));

        account.getRoles().remove(Role.valueOf(roleName));

        return accountRepository.save(account);
    }

    public void isActive(Account account) {
        if(account.getStatus() != AccountStatus.ACTIVE) {
            log.warn("Account not active | accountId={} | status={}", account.getId(), account.getStatus());
            throw new AccountNotActiveException(account.getId());
        }
    }
}
