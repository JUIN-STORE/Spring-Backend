package store.juin.api.domain.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.domain.entity.Order;
import store.juin.api.domain.enums.OrderStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
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
