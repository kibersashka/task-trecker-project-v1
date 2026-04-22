package com.task.tracker.authimpl.accountDetails.model;

import com.task.tracker.authimpl.entity.Account;
import com.task.tracker.authimpl.role.AccountStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record AccountDetails(Account account) implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_%s".formatted(account.getRole().name())));
    }

    @Override
    public @Nullable String getPassword() {
        return account.getHashedPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return AccountStatus.ACTIVE.equals(account.getStatus());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !AccountStatus.SUSPENDED.equals(account.getStatus());
    }

    @Override
    public boolean isEnabled() {
        return AccountStatus.ACTIVE.equals(account.getStatus());
    }
}
