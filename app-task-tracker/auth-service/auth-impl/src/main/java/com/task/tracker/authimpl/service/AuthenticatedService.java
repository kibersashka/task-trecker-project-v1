package com.task.tracker.authimpl.service;

import com.task.tracker.authimpl.accountDetails.model.AccountDetails;
import com.task.tracker.authimpl.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticatedService {
    private final AuthenticationManager authManager;

    public Account authenticate(String username, String password) {
        return ((AccountDetails)
                (
                        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
                        )
                        .getPrincipal()
                )
        ).account();
    }
}
