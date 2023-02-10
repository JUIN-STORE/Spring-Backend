package store.juin.api.domain;


import store.juin.api.domain.request.AddressRequest;

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
}

