package store.juin.api.domain.request;

import store.juin.api.config.SecurityConfig;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.enums.AccountRole;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AccountRequest {
    @Data @Accessors(chain = true)
    public static class SignUp implements Serializable{
        private String email;

        private String passwordHash;

        private String name;

        @Pattern(regexp = "[0-9]{10,11}", message = "10~11자리의 숫자만 입력가능합니다")
        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        private AddressRequest.Register address;

        public Account toAccount(){
            return Account.builder()
                    .email(this.email)
                    .passwordHash(SecurityConfig.makePasswordHash(this.passwordHash))
                    .name(this.name)
                    .phoneNumber(this.phoneNumber)
                    .accountRole(this.accountRole)
                    .lastLogin(LocalDateTime.now())
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class Login implements Serializable {
        private String email;
        private String passwordHash;
    }

    @Data @Accessors(chain = true)
    public static class Update {
        private String passwordHash;

        private String name;

        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public Account toAccount(Long id, String email) {
            return Account.builder()
                    .id(id)
                    .email(email)
                    .name(this.name)
                    .phoneNumber(this.phoneNumber)
                    .passwordHash(SecurityConfig.makePasswordHash(this.passwordHash))
                    .accountRole(this.accountRole)
                    .build();
        }
    }
}