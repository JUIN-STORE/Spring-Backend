package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Address;
import lombok.Data;
import lombok.experimental.Accessors;

public class AddressResponse {
    @Data @Accessors(chain = true)
    public static class ReadResponse {
        private Long id;

        private String city;

        private String street;

        private Integer zipCode;

        private boolean defaultAddress;

        public static ReadResponse fromAddress(Address entity) {
            return new ReadResponse()
                    .setId(entity.getId())
                    .setCity(entity.getCity())
                    .setStreet(entity.getStreet())
                    .setZipCode(entity.getZipCode())
                    .setDefaultAddress(entity.isDefaultAddress());
        }
    }

    @Data @Accessors(chain = true)
    public static class DeleteResponse {
        private Long id;

        private String city;

        private String street;

        private Integer zipCode;

        public static DeleteResponse fromAddress(Address entity) {
            return new DeleteResponse()
                    .setId(entity.getId())
                    .setCity(entity.getCity())
                    .setStreet(entity.getStreet())
                    .setZipCode(entity.getZipCode());
        }
    }
}
