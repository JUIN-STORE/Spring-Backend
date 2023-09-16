package store.juin.api.account.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.entity.Account;
import store.juin.api.address.model.request.AddressRequest;
import store.juin.api.util.PasswordUtil;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AccountRequest {
    @Data @Accessors(chain = true)
    public static class SignUp {
        private String identification;

        private String email;

        private String passwordHash;

        private String name;

        @Pattern(regexp = "\\d{10,11}", message = "10~11자리의 숫자만 입력가능합니다")
        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        private AddressRequest.Create address;

        public Account toAccount(){
            return Account.builder()
                    .identification(this.identification)
                    .email(this.email)
                    .passwordHash(PasswordUtil.makePasswordHash(this.passwordHash))
                    .name(this.name)
                    .phoneNumber(this.phoneNumber)
                    .accountRole(this.accountRole)
                    .lastLogin(LocalDateTime.now())
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class SignIn implements Serializable {
        private String identification;
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
                    .passwordHash(PasswordUtil.makePasswordHash(this.passwordHash))
                    .accountRole(this.accountRole)
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class SendEmail {
        private String identification;
        private String email;
    }

    @Data @Accessors(chain = true)
    public static class ChangePassword {
        private String email;
        private String passwordHash;

        public String makeEncryptedPassword() {
            return PasswordUtil.makePasswordHash(this.passwordHash);
        }
    }
}