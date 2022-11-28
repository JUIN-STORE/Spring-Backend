package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.enums.OrderStatus;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderRequest {
    @Data @Accessors(chain = true)
    public static class Create {
        private List<Long> productIdList;

        private int count;

        @Enumerated(EnumType.STRING)
        private OrderStatus orderStatus; // 주문 상태

        private DeliveryRequest.Receiver deliveryReceiver;

        private AddressRequest.Register deliveryAddress;

        public Order toOrder(){
            return Order.builder()
                    .orderStatus(this.orderStatus)
                    .orderDate(LocalDateTime.now())
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class Read {
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        private OrderStatus orderStatus;
    }

    @Data @Accessors(chain = true)
    public static class Cancel {
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