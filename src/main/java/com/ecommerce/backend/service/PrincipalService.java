package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalService implements UserDetailsService {
    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final Account account = accountService.readByEmail(email);

        return new User(account.getEmail(), account.getPasswordHash(), new ArrayList<>());
    }

    public Account readByPrincipal(Principal principal) {
        final String email = principal.getName();

        return accountService.readByEmail(email);
    }
}