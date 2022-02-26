package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.enums.AccountRole;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountRequest {
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest{
        private String email;
        private String passwordHash;
        private String name;

        //            @PhoneNumber

        private String phoneNumber;
        @CreationTimestamp
        private LocalDateTime registeredAt;

        private LocalDateTime lastLogin;

        @Column(columnDefinition = "ENUM('USER','SELLER','ADMIN')")
        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

//        private Address addresses;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest implements Serializable {
        private String email;
        private String passwordHash;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String email;

        private String passwordHash;

        private String new_passwordHash;

        private String name;


        @Column(columnDefinition = "VARCHAR")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthday;

        private String phoneNumber;
    }
}