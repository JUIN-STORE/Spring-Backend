package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.config.SecurityConfig;
import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.enums.AccountRole;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AccountRequest {
    @Data @Accessors(chain = true)
    public static class RegisterRequest implements Serializable{
        private String email;

        private String passwordHash;

        private String name;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        private AddressRequest.RegisterAddress address;

        public Account toAccount(){
            return Account.builder()
                    .email(email)
                    .passwordHash(SecurityConfig.makePasswordHash(passwordHash))
                    .name(name)
                    .accountRole(accountRole)
                    .lastLogin(LocalDateTime.now())
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class LoginRequest implements Serializable {
        private String email;
        private String passwordHash;
    }

    @Data @Accessors(chain = true)
    public static class UpdateRequest {
        private Long id;

        private String newPasswordHash;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public Account toAccount(Account account) {
            return Account.builder()
                    .id(id)
                    .email(account.getEmail())
                    .name(account.getName())
                    .passwordHash(SecurityConfig.makePasswordHash(newPasswordHash))
                    .accountRole(accountRole)
                    .build();
        }
    }
}