package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

public class AddressRequest {
    @NoArgsConstructor
    @Getter @Setter @Accessors(chain = true)
    public static class CreateRequest implements Serializable{
        private Long id;

        private Long accountId;

        private String city;

        private String street;

        private String zipCode;

        private boolean defaultAddress;

        public static Address toAddress(Account account, AddressRequest.CreateRequest request){
            return Address.builder()
                    .account(account)
                    .city(request.getCity())
                    .street(request.getStreet())
                    .zipCode(request.getZipCode())
                    .defaultAddress(request.isDefaultAddress())
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