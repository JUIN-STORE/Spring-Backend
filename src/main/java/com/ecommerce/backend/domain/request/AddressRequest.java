package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

public class AddressRequest {
    @Data @Accessors(chain = true)
    public static class RegisterRequest implements Serializable {
        private String city;

        private String street;

        private Integer zipCode;

        public Address toAddress(Account account){
            return Address.builder()
                    .account(account)
                    .city(city)
                    .street(street)
                    .zipCode(zipCode)
                    .defaultAddress(false)
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class UpdateRequest implements Serializable {
        private String city;

        private String street;

        private Integer zipCode;

        private boolean defaultAddress;

        public Address toAddress(Account account){
            return Address.builder()
                    .city(city)
                    .street(street)
                    .zipCode(zipCode)
                    .defaultAddress(isDefaultAddress())
                    .build();
        }
    }
}