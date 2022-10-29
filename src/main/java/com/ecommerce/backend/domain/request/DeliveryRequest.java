package com.ecommerce.backend.domain.request;

import lombok.Data;
import lombok.experimental.Accessors;

public class DeliveryRequest {
    @Data @Accessors(chain = true)
    public static class Create {
        private String receiverName;

        private String receiverPhoneNumber;

        private String receiverEmail;

    }
}