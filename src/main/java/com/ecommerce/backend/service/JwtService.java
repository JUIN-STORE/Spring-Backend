package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.repository.jpa.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;

// FIXME: 인터셉터로 뽑아야 하는 케이스인가?
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user name not found!"));

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(), account.getPasswordHash(), new ArrayList<>()
        );
    }

    public Account readByPrincipal(Principal principal) {
        final String email = principal.getName();
        return accountRepository
                .findByEmail(email)
                .orElseThrow(EntityNotFoundException::new);
    }
}