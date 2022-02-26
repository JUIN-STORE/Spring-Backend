package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.enums.AccountRole;
import lombok.*;
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
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class CreateResponse {
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
