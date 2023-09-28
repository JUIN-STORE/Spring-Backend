package store.juin.api.account.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.entity.Account;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Accessors(chain = true)
public class AccountSignUpResponse {
    private String identification;

    private String email;

    private String name;

    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    public static AccountSignUpResponse from(Account account) {
        return new AccountSignUpResponse()
                .setIdentification(account.getIdentification())
                .setEmail(account.getEmail())
                .setName(account.getName())
                .setAccountRole(account.getAccountRole());
    }
}
