package store.juin.api.account.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.entity.Account;
import store.juin.api.address.model.request.AddressCreateRequest;
import store.juin.api.util.PasswordUtil;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AccountSignUpRequest {
    private String identification;

    private String email;

    private String passwordHash;

    private String name;

    @Pattern(regexp = "\\d{10,11}", message = "10~11자리의 숫자만 입력가능합니다")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    private AddressCreateRequest address;

    public Account toAccount() {
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