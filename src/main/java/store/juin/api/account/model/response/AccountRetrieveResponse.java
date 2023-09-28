package store.juin.api.account.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.entity.Account;
import store.juin.api.address.model.response.AddressRetrieveResponse;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Accessors(chain = true)
public class AccountRetrieveResponse {
    private Long id;

    private String identification;

    private String email;

    private String name;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    private AddressRetrieveResponse address;

    public static AccountRetrieveResponse from(Account account) {
        return new AccountRetrieveResponse()
                .setId(account.getId())
                .setIdentification(account.getIdentification())
                .setEmail(account.getEmail())
                .setName(account.getName())
                .setPhoneNumber(account.getPhoneNumber())
                .setAccountRole(account.getAccountRole())
                .setAddress(AddressRetrieveResponse.from(account.getAddressList().get(0)));
    }
}
