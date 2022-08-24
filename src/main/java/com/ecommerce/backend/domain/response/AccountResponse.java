package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.enums.AccountRole;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AccountResponse {
    @Data @Accessors(chain = true)
    public static class SignUp {
        private String email;

        private String name;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public static SignUp fromAccount(Account account) {
            return new SignUp()
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setAccountRole(account.getAccountRole());
        }
    }

    @Data @Accessors(chain = true)
    public static class Read {
        private Long id;

        private String email;

        private String name;

        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        private AddressResponse.ReadResponse address;

        public static Read fromAccount(Account account) {
            return new Read()
                    .setId(account.getId())
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setPhoneNumber(account.getPhoneNumber())
                    .setAccountRole(account.getAccountRole())
                    .setAddress(AddressResponse.ReadResponse.fromAddress(account.getAddressList().get(0)));
        }
    }

    @Data @Accessors(chain = true)
    public static class Remove {
        private Long id;

        private String email;

        private String name;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public static Remove fromAccount(Account account) {
            return new Remove()
                    .setId(account.getId())
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setAccountRole(account.getAccountRole());
        }
    }

    @Data @Accessors(chain = true)
    public static class Login implements Serializable {
        private String email;

        private String token;

        public static Login fromAccount(String email, String token) {
            return new Login()
                    .setEmail(email)
                    .setToken("Bearer " + token);
        }
    }

    @Data @Accessors(chain = true)
    public static class Modify {
        private String email;

        private String name;

        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        @LastModifiedDate
        private LocalDateTime updatedAt;

        public static Modify fromAccount(Account account) {
            return new Modify()
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setPhoneNumber(account.getPhoneNumber())
                    .setAccountRole(account.getAccountRole())
                    .setUpdatedAt(LocalDateTime.now());
        }
    }
}
