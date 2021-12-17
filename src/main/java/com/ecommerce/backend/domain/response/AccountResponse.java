package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.enums.AccountType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

public class AccountResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class ReadResponse {
        private Long id;

        private String email;

        private String lastName; // 성

        @CreationTimestamp
        private LocalDateTime registeredAt;

        private LocalDateTime lastLogin;

        @Enumerated(EnumType.STRING)
        private AccountType accountType;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class CreateResponse {
        private Long id;

        private String email;

        private String lastName; // 성

        @CreationTimestamp
        private LocalDateTime registeredAt;

        private LocalDateTime lastLogin;

        @Enumerated(EnumType.STRING)
        private AccountType accountType;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class DeleteResponse {
        private Long id;

        private String email;

        private String lastName; // 성

        @CreationTimestamp
        private LocalDateTime registeredAt;

        private LocalDateTime lastLogin;

        @Enumerated(EnumType.STRING)
        private AccountType accountType;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class LoginResponse{
        private String username;
        // class GrantedAuthority를 상속하는 class만 가능

        private String token;
    }
}
