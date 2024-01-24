package store.juin.api.common;


import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.request.AccountSignInRequest;
import store.juin.api.account.model.request.AccountSignUpRequest;
import store.juin.api.account.model.request.AccountUpdateRequest;
import store.juin.api.address.model.request.AddressCreateRequest;
import store.juin.api.address.model.request.AddressUpdateRequest;
import store.juin.api.cart.model.request.CategoryCreateRequest;
import store.juin.api.cartitem.model.request.CartItemAddRequest;
import store.juin.api.cartitem.model.request.CartItemUpdateRequest;
import store.juin.api.delivery.model.request.DeliveryReceiverRequest;
import store.juin.api.item.model.request.ItemCreateRequest;
import store.juin.api.order.enumeration.OrderStatus;
import store.juin.api.order.model.request.OrderCreateRequest;
import store.juin.api.order.model.request.OrderRetrieveRequest;

import java.time.LocalDate;
import java.util.List;

public class RequestUtil {

    public static AddressUpdateRequest makeAddressUpdateRequest(boolean defaultAddress) {
        return new AddressUpdateRequest()
                .setAddressId(3L)
                .setCity("서울시")
                .setStreet("강남구")
                .setZipCode(12345)
                .setDefaultAddress(defaultAddress);
    }

    public static AddressCreateRequest makeCreateRequest(boolean defaultAddress) {
        return new AddressCreateRequest()
                .setCity("서울시")
                .setStreet("강남구")
                .setZipCode(12345)
                .setDefaultAddress(defaultAddress);
    }

    public static CartItemAddRequest makeCartItemAddRequest() {
        return new CartItemAddRequest()
                .setItemId(1L)
                .setCount(3);
    }

    public static CartItemUpdateRequest makeCartItemUpdateRequest() {
        return new CartItemUpdateRequest()
                .setItemId(1L)
                .setCount(3);
    }

    public static CategoryCreateRequest makeCategoryCreateRequest() {
        return new CategoryCreateRequest()
                .setCategoryName("카테고리")
                .setParentId(1L);
    }

    public static AddressCreateRequest makeAddressCreateRequest(boolean defaultAddress) {
        return new AddressCreateRequest()
                .setCity("서울시")
                .setStreet("강남구")
                .setZipCode(12345)
                .setDefaultAddress(defaultAddress);
    }

    public static DeliveryReceiverRequest makeDeliveryReceiver() {
        return new DeliveryReceiverRequest()
                .setReceiverName("홍길동")
                .setReceiverPhoneNumber("010-1234-5678")
                .setReceiverEmail("junsu0325@naver.com");
    }

    public static OrderCreateRequest makeOrderCreateRequest() {
        return new OrderCreateRequest()
                .setItemIdList(List.of(1L, 2L))
                .setCount(3)
                .setGrandTotal(30000)
                .setOrderStatus(OrderStatus.ORDER)
                .setDeliveryReceiver(makeDeliveryReceiver())
                .setDeliveryAddress(makeAddressCreateRequest(true));
    }


    public static OrderRetrieveRequest makeOrderRetrieveRequest() {
        return new OrderRetrieveRequest()
                .setStartDate(LocalDate.of(2022, 2, 22))
                .setEndDate(LocalDate.of(2022, 3, 25))
                .setOrderStatus(OrderStatus.ORDER);
    }

    public static ItemCreateRequest makeItemCreateRequest() {
        return new ItemCreateRequest()
                .setCategoryId(1L)
                .setName("REAL FORCE R3")
                .setPrice(1000_000)
                .setQuantity(300)
                .setDescription("REAL REAL REAL FORCE R3");
    }

    public static AccountSignUpRequest makeSignUpRequest() {
        var request = new AccountSignUpRequest();
        request.setIdentification("junsu0325");
        request.setEmail("js@gmail.com");
        request.setPasswordHash("asdq13@#13$");
        request.setName("준수");
        request.setPhoneNumber("010-1111-2222");
        request.setAccountRole(AccountRole.ADMIN);

        var addressRequest = new AddressCreateRequest();
        addressRequest.setCity("도시");
        addressRequest.setStreet("상세 주소");
        addressRequest.setZipCode(12345);

        request.setAddress(addressRequest);
        return request;
    }

    public static AccountSignInRequest makeSignInRequest(String identification, String passwordHash) {
        return new AccountSignInRequest()
                .setIdentification(identification)
                .setPasswordHash(passwordHash);
    }

    public static AccountUpdateRequest makeAccountUpdateRequest(String passwordHash, String name
                                                               , String phoneNumber, AccountRole accountRole) {
        return new AccountUpdateRequest()
                .setPasswordHash(passwordHash)
                .setName(name)
                .setPhoneNumber(phoneNumber)
                .setAccountRole(accountRole);
    }
}