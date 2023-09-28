package store.juin.api.order.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.order.enumeration.OrderStatus;
import store.juin.api.order.model.entity.Order;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class OrderCreateResponse {
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
