package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

public class AddressRequest {
    @NoArgsConstructor
    @Getter @Setter @Accessors(chain = true)
    public static class RegisterAddress implements Serializable{
        private String city;

        private String street;

        private String zipCode;

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

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressUpdateReq {
        private Long accountId;

        private String city;

        private String street;

        private String zipCode;

        private boolean defaultAddress;
    }
}