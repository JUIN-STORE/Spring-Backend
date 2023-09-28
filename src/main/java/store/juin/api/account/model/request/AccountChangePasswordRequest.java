package store.juin.api.account.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.util.PasswordUtil;

@Data
@Accessors(chain = true)
public class AccountChangePasswordRequest {
    private String email;
    private String passwordHash;

    public String makeEncryptedPassword() {
        return PasswordUtil.makePasswordHash(this.passwordHash);
    }
}