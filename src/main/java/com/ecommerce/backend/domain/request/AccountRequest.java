package com.ecommerce.backend.domain.request;

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

        private AddressRequest.RegisterRequest address;

        public Account toAccount(){
            return Account.builder()
                    .email(email)
                    .passwordHash(passwordHash)
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
        private String name;

        private String passwordHash;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public Account toAccount(Long pId, String pEmail) {
            return Account.builder()
                    .id(pId)
                    .email(pEmail)
                    .name(name)
                    .passwordHash(passwordHash)
                    .accountRole(accountRole)
                    .build();
        }
    }
}