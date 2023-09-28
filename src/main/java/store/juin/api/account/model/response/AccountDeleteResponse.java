package store.juin.api.account.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.entity.Account;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Accessors(chain = true)
public class AccountDeleteResponse {
    private Long id;

    private String identification;

    private String email;

    private String name;

    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    public static AccountDeleteResponse from(Account account) {
        return new AccountDeleteResponse()
                .setId(account.getId())
                .setEmail(account.getEmail())
                .setName(account.getName())
                .setAccountRole(account.getAccountRole());
    }
}
