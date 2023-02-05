package com.juin.store.service.query;

import com.juin.store.domain.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalQueryService {
    private final AccountQueryService accountQueryService;

    @Transactional(readOnly = true)
    public Account readByPrincipal(Principal principal) {
        final String email = principal.getName();

        return accountQueryService.readByEmail(email);
    }
}