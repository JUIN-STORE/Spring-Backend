package store.juin.api.domain;


import store.juin.api.domain.request.AddressRequest;
import store.juin.api.domain.request.CartItemRequest;
import store.juin.api.domain.request.CategoryRequest;

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
}

