package store.juin.api.address.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.account.model.entity.Account;
import store.juin.api.address.model.entity.Address;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class AddressCreateRequest {
    @NotBlank
    private String city;

    @NotBlank
    private String street;

    @NotBlank
    private Integer zipCode;

    private boolean defaultAddress;

    public Address toAddress(Account account) {
        return Address.builder()
                .account(account)
                .city(city)
                .street(street)
                .zipCode(zipCode)
                .defaultAddress(defaultAddress)
                .build();
    }
}