package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.repository.jpa.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final Account account = accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user name not found!"));

        return new User(account.getEmail(), account.getPasswordHash(), new ArrayList<>());
    }

    public Account readByPrincipal(Principal principal) {
        final String email = principal.getName();

        return accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("email not found by principal!"));
    }
}