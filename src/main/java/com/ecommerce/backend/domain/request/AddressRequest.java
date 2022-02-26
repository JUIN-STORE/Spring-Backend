package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Account;
import lombok.*;

public class AddressRequest {
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest{
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
    public static class UpdateRequest {
        private Account account;

        private String city;

        private String street;

        private String zipCode;

        private boolean defaultAddress;
    }
}