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

        public static SignUp from(Account account) {
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

        private AddressResponse.Read address;

        public static Read from(Account account) {
            return new Read()
                    .setId(account.getId())
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setPhoneNumber(account.getPhoneNumber())
                    .setAccountRole(account.getAccountRole())
                    .setAddress(AddressResponse.Read.from(account.getAddressList().get(0)));
        }
    }

    @Data @Accessors(chain = true)
    public static class Delete {
        private Long id;

        private String email;

        private String name;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public static Delete from(Account account) {
            return new Delete()
                    .setId(account.getId())
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setAccountRole(account.getAccountRole());
        }
    }

    @Data @Accessors(chain = true)
    public static class Login implements Serializable {
        private String email;

        private TokenResponse token;

        public static Login of(String email, String accessToken) {
            return new Login()
                    .setEmail(email)
                    .setToken(TokenResponse.of(accessToken));
        }
    }

    @Data @Accessors(chain = true)
    public static class Update {
        private String email;

        private String name;

        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        @LastModifiedDate
        private LocalDateTime updatedAt;

        public static Update from(Account account) {
            return new Update()
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setPhoneNumber(account.getPhoneNumber())
                    .setAccountRole(account.getAccountRole())
                    .setUpdatedAt(LocalDateTime.now());
        }
    }
}
