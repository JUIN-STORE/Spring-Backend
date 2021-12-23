package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.enums.AccountType;
import com.ecommerce.backend.domain.enums.GenderType;
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
    public static class CreateRequest {
        private Long accountId;

        private String email;
        private String passwordHash;
        private String firstName;
        private String lastName;

        @Column(columnDefinition = "ENUM('MALE','FEMALE')")
        @Enumerated(EnumType.STRING)
        private GenderType gender;

        @Column(columnDefinition = "VARCHAR")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthday;

        //            @PhoneNumber
        private String phoneNumber;

        @CreationTimestamp
        private LocalDateTime registeredAt;

        private LocalDateTime lastLogin;

        @Column(columnDefinition = "ENUM('USER','SELLER','ADMIN')")
        @Enumerated(EnumType.STRING)
        private AccountType accountType;

//        private Address defaultAddress;
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
    public static class CreateAccountRequest {
        private String email;
        private String passwordHash;
        private String lastname;
        private String firstname;
        private GenderType gender;
        private AccountType accounttype;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAccountRequest {
        private Long accountId;
        private String email;
        private String passwordHash;
        private String new_passwordHash;
        private String lastname;
        private String firstname;
        @Column(columnDefinition = "ENUM('MALE','FEMALE')")
        @Enumerated(EnumType.STRING)
        private GenderType gender;
        @Column(columnDefinition = "VARCHAR")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthday;
        private String phoneNumber;
    }
}