package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.enums.AccountRole;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AccountResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class ReadResponse {
        private Long id;

        private String email;

        private String name;

        @CreationTimestamp
        private LocalDateTime registeredAt;

        private LocalDateTime lastLogin;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;
    }

    @NoArgsConstructor
    @AllArgsConstructor @ToString
    @Getter @Setter @Accessors(chain = true)
    public static class CreateResponse {
        private Long id;

        private String email;

        private String name;

        @CreationTimestamp
        private LocalDateTime registeredAt;

        private LocalDateTime lastLogin;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public static CreateResponse fromAccount(Account account) {
            return new CreateResponse()
                    .setId(account.getId())
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setRegisteredAt(account.getRegisteredAt())
                    .setLastLogin(account.getLastLogin())
                    .setAccountRole(account.getAccountRole());
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class DeleteResponse {
        private Long id;

        private String email;

        private String name;

        @CreationTimestamp
        private LocalDateTime registeredAt;

        private LocalDateTime lastLogin;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class LoginResponse implements Serializable {
        private String token;
    }
}
