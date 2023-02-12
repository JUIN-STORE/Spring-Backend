package store.juin.api.domain;

import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Address;
import store.juin.api.domain.entity.Delivery;
import store.juin.api.domain.entity.DeliveryReceiver;
import store.juin.api.domain.enums.DeliveryStatus;

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
                .city("서울시 강남구 테헤란로 123")
                .street("어디 건물 8층")
                .zipCode(12345)
                .defaultAddress(defaultAddress)
                .build();
    }

    public static Delivery makeDelivery(Long id) {
        return Delivery.builder()
                .id(id)
                .deliveryReceiver(makeDeliveryReceiver())
                .deliveryAddress(makeAddress(true))
                .deliveryStatus(DeliveryStatus.READY)
                .build();
    }

    public static DeliveryReceiver makeDeliveryReceiver() {
        return DeliveryReceiver.builder()
                .receiverName("황준수")
                .receiverEmail("js@gmail.com")
                .receiverPhoneNumber("010-1234-5678")
                .build();
    }
}
