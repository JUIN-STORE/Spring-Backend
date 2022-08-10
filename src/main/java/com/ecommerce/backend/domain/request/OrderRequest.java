package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.enums.OrderStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

public class OrderRequest {
    @Data @Accessors(chain = true)
    public static class Create {
        private List<Long> productIdList;

        private int count;

        @Enumerated(EnumType.STRING)
        private OrderStatus orderStatus; // 주문 상태

        public Order toOrder(){
            return Order.builder()
                    .orderStatus(this.orderStatus)
                    .orderDate(LocalDateTime.now())
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class CancelRequest {
        private Long productId;

        private int count;

        @Enumerated(EnumType.STRING)
        private OrderStatus orderStatus; // 주문 상태

        public Order toOrder(){
            return Order.builder()
                    .orderStatus(this.orderStatus)
                    .orderDate(LocalDateTime.now())
                    .build();
        }
    }
}