package store.juin.api.domain.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Address;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AddressRequest {
    @Data @Accessors(chain = true)
    public static class Create {
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
    public static class Update {
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