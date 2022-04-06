package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Address;
import lombok.Data;
import lombok.experimental.Accessors;

public class AddressResponse {
    @Data @Accessors(chain = true)
    public static class AddressRead {
        private Long id;

        private String city;

        private String street;

        private String zipCode;

        private boolean defaultAddress;

        public static AddressRead fromAddress(Address address) {
            return new AddressRead()
                    .setId(address.getId())
                    .setCity(address.getCity())
                    .setStreet(address.getStreet())
                    .setZipCode(address.getZipCode())
                    .setDefaultAddress(address.isDefaultAddress());
        }
    }

    @Data @Accessors(chain = true)
    public static class AddressCreate {
        private Long id;

        public static AddressCreate fromAddress(Address address) {
            return new AddressCreate()
                    .setId(address.getId());
        }
    }

    @Data @Accessors(chain = true)
    public static class AddressDelete {
        private Long id;

        public static AddressDelete fromAddress(Address entity) {
            return new AddressDelete()
                    .setId(entity.getId());
        }
    }
}
