package store.juin.api.address.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.address.model.entity.Address;

@Data
@Accessors(chain = true)
public class AddressRetrieveResponse {
    private Long id;

    private String city;

    private String street;

    private Integer zipCode;

    private boolean defaultAddress;

    public static AddressRetrieveResponse from(Address address) {
        return new AddressRetrieveResponse()
                .setId(address.getId())
                .setCity(address.getCity())
                .setStreet(address.getStreet())
                .setZipCode(address.getZipCode())
                .setDefaultAddress(address.isDefaultAddress());
    }
}
