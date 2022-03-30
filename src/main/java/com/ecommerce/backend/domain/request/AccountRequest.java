package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.enums.AccountRole;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AccountRequest {

    @NoArgsConstructor @ToString
    @Getter @Setter @Accessors(chain = true)
    public static class CreateRequest{
        private String email;
        private String passwordHash;
        private String name;

        @Column(columnDefinition = "ENUM('USER','SELLER','ADMIN')")
        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        private Address addresses;

        public static Account toAccount(CreateRequest request){
            return Account.builder()
                    .email(request.getEmail())
                    .passwordHash(new BCryptPasswordEncoder().encode(request.getPasswordHash()))
                    .name(request.getName())
                    .accountRole(request.getAccountRole())
                    .registeredAt(LocalDateTime.now())
                    .lastLogin(LocalDateTime.now())
                    .build();
        }
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
    }
}