package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Account;
import lombok.*;

public class AddressReq {
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressCreateReq {
        private Long id;

        private Account account;

        private String city;

        private String street;

        private String zipCode;

        private boolean defaultAddress = true;
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