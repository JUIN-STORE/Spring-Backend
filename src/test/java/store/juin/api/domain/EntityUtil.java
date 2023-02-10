package store.juin.api.domain;

import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Address;

public class EntityUtil {
    public static Account makeAccount() {
        return Account.builder()
                .id(9L)
                .email("js@gmail.com")
                .passwordHash("this is a password")
                .build();
    }

    public static Address makeAddress(boolean defaultAddress) {
        return Address.builder()
                .id(3L)
                .city("서울시")
                .street("강남구")
                .zipCode(12345)
                .defaultAddress(defaultAddress)
                .build();
    }
}
