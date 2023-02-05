package store.juin.api.domain.response;

import store.juin.api.domain.entity.Address;
import lombok.Data;
import lombok.experimental.Accessors;

public class AddressResponse {
    @Data @Accessors(chain = true)
    public static class Retrieve {
        private Long id;

        private String city;

        private String street;

        private Integer zipCode;

        private boolean defaultAddress;

        public static Retrieve from(Address address) {
            return new Retrieve()
                    .setId(address.getId())
                    .setCity(address.getCity())
                    .setStreet(address.getStreet())
                    .setZipCode(address.getZipCode())
                    .setDefaultAddress(address.isDefaultAddress());
        }
    }

    @Data @Accessors(chain = true)
    public static class Delete {
        private Long id;

        private String city;

        private String street;

        private Integer zipCode;

        public static Delete from(Address address) {
            return new Delete()
                    .setId(address.getId())
                    .setCity(address.getCity())
                    .setStreet(address.getStreet())
                    .setZipCode(address.getZipCode());
        }
    }
}
