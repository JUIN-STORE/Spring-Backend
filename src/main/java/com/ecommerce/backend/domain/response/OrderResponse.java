package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.enums.OrderStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class OrderResponse {

    @Data @Accessors(chain = true)
    public static class CreateResponse {
        private Long id;

        private int grandTotal;

        @Enumerated(EnumType.STRING)
        private OrderStatus orderStatus; // 주문 상태

        public static CreateResponse fromOrder(Order entity) {
            return new CreateResponse()
                    .setId(entity.getId())
                    .setGrandTotal(entity.getGrandTotal());
        }
    }
}
