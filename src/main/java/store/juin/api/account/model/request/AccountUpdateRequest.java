package store.juin.api.account.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.entity.Account;
import store.juin.api.util.PasswordUtil;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Accessors(chain = true)
public class AccountUpdateRequest {
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
