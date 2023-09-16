package store.juin.api.common;


import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.request.AccountRequest;
import store.juin.api.address.model.request.AddressRequest;
import store.juin.api.cart.model.request.CategoryRequest;
import store.juin.api.cartitem.model.request.CartItemRequest;
import store.juin.api.delivery.model.request.DeliveryRequest;
import store.juin.api.item.model.request.ItemRequest;
import store.juin.api.order.enumeration.OrderStatus;
import store.juin.api.order.model.request.OrderRequest;

import java.time.LocalDate;
import java.util.List;

public class RequestUtil {

    public static AddressRequest.Update makeAddressUpdateRequest(boolean defaultAddress) {
        return new AddressRequest.Update()
                .setAddressId(3L)
                .setCity("서울시")
                .setStreet("강남구")
                .setZipCode(12345)
                .setDefaultAddress(defaultAddress);
    }

    public static AddressRequest.Create makeCreateRequest(boolean defaultAddress) {
        return new AddressRequest.Create()
                .setCity("서울시")
                .setStreet("강남구")
                .setZipCode(12345)
                .setDefaultAddress(defaultAddress);
    }

    public static CartItemRequest.Add makeCartItemAddRequest() {
        return new CartItemRequest.Add()
                .setItemId(1L)
                .setCount(3);
    }

    public static CartItemRequest.Update makeCartItemUpdateRequest() {
        return new CartItemRequest.Update()
                .setItemId(1L)
                .setCount(3);
    }

    public static CategoryRequest.Create makeCategoryCreateRequest() {
        return new CategoryRequest.Create()
                .setCategoryName("카테고리")
                .setParentId(1L);
    }

    public static AddressRequest.Create makeAddressCreateRequest(boolean defaultAddress) {
        return new AddressRequest.Create()
                .setCity("서울시")
                .setStreet("강남구")
                .setZipCode(12345)
                .setDefaultAddress(defaultAddress);
    }

    public static DeliveryRequest.Receiver makeDeliveryReceiver() {
        return new DeliveryRequest.Receiver()
                .setReceiverName("홍길동")
                .setReceiverPhoneNumber("010-1234-5678")
                .setReceiverEmail("junsu0325@naver.com");
    }

    public static OrderRequest.Create makeOrderCreateRequest() {
        return new OrderRequest.Create()
                .setItemIdList(List.of(1L, 2L))
                .setCount(3)
                .setGrandTotal(30000)
                .setOrderStatus(OrderStatus.ORDER)
                .setDeliveryReceiver(makeDeliveryReceiver())
                .setDeliveryAddress(makeAddressCreateRequest(true));
    }


    public static OrderRequest.Retrieve makeOrderRetrieveRequest() {
        return new OrderRequest.Retrieve()
                .setStartDate(LocalDate.of(2022, 2, 22))
                .setEndDate(LocalDate.of(2022, 3, 25))
                .setOrderStatus(OrderStatus.ORDER);
    }

    public static ItemRequest.Create makeItemCreateRequest() {
        return new ItemRequest.Create()
                .setCategoryId(1L)
                .setName("REAL FORCE R3")
                .setPrice(1000_000)
                .setQuantity(300)
                .setDescription("REAL REAL REAL FORCE R3");
    }

    public static AccountRequest.SignUp makeSignUpRequest() {
        var request = new AccountRequest.SignUp();
        request.setEmail("js@gmail.com");
        request.setPasswordHash("asdq13@#13$");
        request.setName("준수");
        request.setPhoneNumber("010-1111-2222");
        request.setAccountRole(AccountRole.ADMIN);

        var addressRequest = new AddressRequest.Create();
        addressRequest.setCity("도시");
        addressRequest.setStreet("상세 주소");
        addressRequest.setZipCode(12345);

        request.setAddress(addressRequest);
        return request;
    }

    public static AccountRequest.SignIn makeSignInRequest(String identification, String passwordHash) {
        return new AccountRequest.SignIn()
                .setIdentification(identification)
                .setPasswordHash(passwordHash);
    }

    public static AccountRequest.Update makeAccountUpdateRequest(String passwordHash, String name
                                                               , String phoneNumber, AccountRole accountRole) {
        return new AccountRequest.Update()
                .setPasswordHash(passwordHash)
                .setName(name)
                .setPhoneNumber(phoneNumber)
                .setAccountRole(accountRole);
    }
}