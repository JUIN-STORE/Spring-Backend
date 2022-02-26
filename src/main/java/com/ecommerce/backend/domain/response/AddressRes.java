package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.enums.AccountRole;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

public class AddressRes {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class AddressReadRes {
        private Long id;

        private Account account;

        private String city;

        private String street;

        private String zipCode;

        private boolean defaultAddress;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class AddressCreateRes {
        private Long id;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Builder
    public static class AddressUpdateRes {
        private Long id;

        private String email;

        private String lastName; // 성

        @CreationTimestamp
        private LocalDateTime registeredAt;

        private LocalDateTime lastLogin;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;
    }


}
