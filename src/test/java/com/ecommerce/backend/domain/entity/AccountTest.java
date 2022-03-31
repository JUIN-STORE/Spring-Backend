package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

@Transactional
@SpringBootTest
class AccountTest {
    @Autowired
    private AccountRepository accountRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Auditing 테스트")
    @WithMockUser(username = "junsu", roles = "USER")
    public void auditingTest() {
        Account newAccount = createAccount();
        accountRepository.save(newAccount);

        em.flush();
        em.clear();

        Account account = accountRepository.findById(newAccount.getId())
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("updatedAt = " + account.getUpdatedAt());
        System.out.println("registeredAt = " + account.getRegisteredAt());
    }

    public Account createAccount() {
        return Account.builder()
                .email("cart@test.com")
                .passwordHash("test")
                .name("릴러말즈")
                .accountRole(AccountRole.USER)
                .lastLogin(LocalDateTime.now())
                .build();
    }
}