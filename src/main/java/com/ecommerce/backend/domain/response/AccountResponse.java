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
    public static class RegisterResponse {
        private String email;

        private String name;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public static RegisterResponse fromAccount(Account account) {
            return new RegisterResponse()
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setAccountRole(account.getAccountRole());
        }
    }

    @Data @Accessors(chain = true)
    public static class ReadResponse {
        private Long id;

        private String email;

        private String name;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public static ReadResponse fromAccount(Account account) {
            return new ReadResponse()
                    .setId(account.getId())
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setAccountRole(account.getAccountRole());
        }
    }

    @Data @Accessors(chain = true)
    public static class DeleteResponse {
        private Long id;

        private String email;

        private String name;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public static DeleteResponse fromAccount(Account account) {
            return new DeleteResponse()
                    .setId(account.getId())
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setAccountRole(account.getAccountRole());
        }
    }

    @Data @Accessors(chain = true)
    public static class LoginResponse implements Serializable {
        private String token;

        public static LoginResponse fromAccount(String token) {
            return new LoginResponse()
                    .setToken(token);
        }
    }

    @Data @Accessors(chain = true)
    public static class UpdateResponse {
        private String email;

        private String name;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        @LastModifiedDate
        private LocalDateTime updatedAt;

        public static UpdateResponse fromAccount(Account account) {
            return new UpdateResponse()
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setAccountRole(account.getAccountRole())
                    .setUpdatedAt(LocalDateTime.now());
        }
    }
}
