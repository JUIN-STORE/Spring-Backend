package store.juin.api.domain.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.LastModifiedDate;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.enums.AccountRole;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountResponse {
    @Data @Accessors(chain = true)
    public static class SignUp {
        private String identification;

        private String email;

        private String name;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        public static SignUp from(Account account) {
            return new SignUp()
                    .setIdentification(account.getIdentification())
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setAccountRole(account.getAccountRole());
        }
    }

    @Data @Accessors(chain = true)
    public static class Retrieve {
        private Long id;

        private String identification;

        private String email;

        private String name;

        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;

        private AddressResponse.Retrieve address;

        public static Retrieve from(Account account) {
            return new Retrieve()
                    .setId(account.getId())
                    .setIdentification(account.getIdentification())
                    .setEmail(account.getEmail())
                    .setName(account.getName())
                    .setPhoneNumber(account.getPhoneNumber())
                    .setAccountRole(account.getAccountRole())
                    .setAddress(AddressResponse.Retrieve.from(account.getAddressList().get(0)));
        }
    }

    @Data @Accessors(chain = true)
    public static class Delete {
        private Long id;

        private String identification;

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
    public static class SignIn implements Serializable {
        private String identification;

        private TokenResponse token;

        public static SignIn of(String identification, String accessToken) {
            return new SignIn()
                    .setIdentification(identification)
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
