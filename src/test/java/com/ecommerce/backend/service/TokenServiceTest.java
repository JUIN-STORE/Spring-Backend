package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.repository.jpa.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class TokenServiceTest {
    @InjectMocks
    private PrincipalService sut;

    @Mock
    private AccountRepository accountRepository;

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsernameTest {

        @Test
        @DisplayName("loadUserByUsername 성공")
        void loadUserByUsernameTest01() {
            // given
            final String expectedEmail = "test@gmail.com";
            final String expectedPasswordHash = "passwordHash1234";

            given(accountRepository.findByEmail(expectedEmail))
                    .willReturn(getAccount(expectedEmail, expectedPasswordHash));

            // when
            final UserDetails userDetails = sut.loadUserByUsername(expectedEmail);
            final String actualEmail = userDetails.getUsername();
            final String actualPassword = userDetails.getPassword();

            // then
            assertEquals(expectedEmail, actualEmail);
            assertEquals(expectedPasswordHash, actualPassword);
        }

        @Test
        @DisplayName("loadUserByUsername 실패")
        void loadUserByUsernameTest02() {
            // given
            given(accountRepository.findByEmail(anyString())).willReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> sut.loadUserByUsername("test"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("user name not found!");
        }
    }

    @Nested
    @DisplayName("readByPrincipal")
    class ReadByPrincipalTest {

        @Test
        @DisplayName("readByPrincipal 성공")
        void readByPrincipalTest01() {
            // given
            final Principal mockPrincipal = Mockito.mock(Principal.class);
            final String expectedEmail = "junsu0325@github.com";
            final String expectedPasswordHash = "passwordHash1234";

            given(mockPrincipal.getName()).willReturn(expectedEmail);
            given(accountRepository.findByEmail(mockPrincipal.getName()))
                    .willReturn(getAccount(expectedEmail, expectedPasswordHash));

            // when
            final Account account = sut.readByPrincipal(mockPrincipal);
            final String actualEmail = account.getEmail();
            final String actualPasswordHash = account.getPasswordHash();

            // then
            assertEquals(expectedEmail, actualEmail);
            assertEquals(expectedPasswordHash, actualPasswordHash);
        }

        @Test
        @DisplayName("readByPrincipal 실패")
        void readByPrincipalTest02() {
            // given
            final Principal mockPrincipal = Mockito.mock(Principal.class);
            given(accountRepository.findByEmail(anyString())).willReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> sut.readByPrincipal(mockPrincipal))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("email not found by principal!");
        }
    }

    private Optional<Account> getAccount(String email, String passwordHash) {
        return Optional.ofNullable(Account.builder().email(email).passwordHash(passwordHash).build());
    }
}