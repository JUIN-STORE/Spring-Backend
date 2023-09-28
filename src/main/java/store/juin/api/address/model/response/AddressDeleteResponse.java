package store.juin.api.address.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.address.model.entity.Address;

@Data
@Accessors(chain = true)
public class AddressDeleteResponse {
    private Long id;

    private String city;

    private String street;

    private Integer zipCode;

    public static AddressDeleteResponse from(Address address) {
        return new AddressDeleteResponse()
                .setId(address.getId())
                .setCity(address.getCity())
                .setStreet(address.getStreet())
                .setZipCode(address.getZipCode());
    }
}
