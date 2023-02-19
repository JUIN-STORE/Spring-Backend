package store.juin.api.domain;

import store.juin.api.domain.entity.*;
import store.juin.api.domain.enums.AccountRole;
import store.juin.api.domain.enums.DeliveryStatus;
import store.juin.api.domain.enums.ItemStatus;

import java.util.List;

public class EntityUtil {
    public static Token makeToken() {
        return Token.builder().build();
    }

    public static Account makeAccount(String passwordHash) {
        return Account.builder()
                .id(9L)
                .identification("junsu0325")
                .email("junsu0325@naver.com")
                .passwordHash(passwordHash)
//                .passwordHash("asdq13@#13$")
                .name("준수")
                .phoneNumber("010-1111-2222")
                .accountRole(AccountRole.ADMIN)
                .addressList(List.of(makeAddress(true)))
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

    public static List<Address> makeAddressList() {
        return List.of(
                Address.builder()
                        .id(1L)
                        .city("서울시")
                        .street("동작구")
                        .zipCode(18297)
                        .defaultAddress(true)
                        .build(),
                Address.builder()
                        .id(2L)
                        .city("서울시")
                        .street("강남구")
                        .zipCode(12345)
                        .defaultAddress(false)
                        .build()
        );
    }

    public static Item makeItem(Long itemId, String name) {
        return Item.builder()
                .id(itemId)
                .name(name)
                .price(10000)
                .quantity(20)
                .soldCount(1)
                .description("아이템 설명")
                .itemStatus(ItemStatus.READY)
                .itemImageList(List.of(makeItemImage()))
                .build();
    }

    public static ItemImage makeItemImage() {
        return ItemImage.builder()
                .id(1L)
                .name("원본 이미지 파일명을 통해 새로 만든 이미지 파일명")
                .originName("원본 오리지날 이름")
                .imageUrl("http://imageUrl.com")
                .thumbnail(false)
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
