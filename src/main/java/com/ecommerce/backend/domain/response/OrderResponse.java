package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.enums.OrderStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class OrderResponse {
    @Data
    @Accessors(chain = true)
    public static class Create {
        private Long id;

        private int grandTotal;

        @Enumerated(EnumType.STRING)
        private OrderStatus orderStatus; // 주문 상태

        public static Create of(Order order) {
            return new Create()
                    .setId(order.getId())
                    .setGrandTotal(order.getGrandTotal());
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Delete {
        private long ordersDeletedCount;
        private long orderItemDeletedCount;

        public static Delete of(long ordersDeleteCount, long orderItemDeleteCount) {
            return new Delete()
                    .setOrdersDeletedCount(ordersDeleteCount)
                    .setOrderItemDeletedCount(orderItemDeleteCount);
        }
    }
}
