package store.juin.api.account.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.LastModifiedDate;
import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.entity.Account;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AccountUpdateResponse {
    private String email;

    private String name;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static AccountUpdateResponse from(Account account) {
        return new AccountUpdateResponse()
                .setEmail(account.getEmail())
                .setName(account.getName())
                .setPhoneNumber(account.getPhoneNumber())
                .setAccountRole(account.getAccountRole())
                .setUpdatedAt(LocalDateTime.now());
    }
}
