package com.juin.store.domain.request;

import com.juin.store.domain.entity.Account;
import com.juin.store.domain.entity.Address;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class AddressRequest {
    @Data @Accessors(chain = true)
    public static class Register implements Serializable {
        @NotBlank
        private String city;

        @NotBlank
        private String street;

        @NotBlank
        private Integer zipCode;

        private boolean defaultAddress;

        public Address toAddress(Account account){
            return Address.builder()
                    .account(account)
                    .city(city)
                    .street(street)
                    .zipCode(zipCode)
                    .defaultAddress(defaultAddress)
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class Update implements Serializable {
        private Long addressId;

        private String city;

        private String street;

        private Integer zipCode;

        private boolean defaultAddress;

        public Address toAddress(Account account){
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
}