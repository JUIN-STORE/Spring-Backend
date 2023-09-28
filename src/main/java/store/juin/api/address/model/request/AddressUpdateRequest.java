package store.juin.api.address.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.account.model.entity.Account;
import store.juin.api.address.model.entity.Address;

@Data
@Accessors(chain = true)
public class AddressUpdateRequest {
    private Long addressId;

    private String city;

    private String street;

    private Integer zipCode;

    private boolean defaultAddress;

    public Address toAddress(Account account) {
        return Address.builder()
                .id(addressId)
                .account(account)
                .city(city)
                .street(street)
                .zipCode(zipCode)
                .defaultAddress(defaultAddress)
                .build();
    }
}
