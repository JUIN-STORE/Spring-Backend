package store.juin.api.domain.request;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import store.juin.api.domain.entity.Order;
import store.juin.api.domain.enums.OrderStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderRequest {
    @Data @Accessors(chain = true)
    public static class Create {
        private List<Long> itemIdList;

        private int count;

        private int grandTotal;

        @Enumerated(EnumType.STRING)
        private OrderStatus orderStatus; // 주문 상태

        private DeliveryRequest.Receiver deliveryReceiver;

        private AddressRequest.Create deliveryAddress;

        public Order toOrder(){
            return Order.builder()
                    .orderStatus(this.orderStatus)
                    .orderDate(LocalDateTime.now())
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class Retrieve {
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        private OrderStatus orderStatus;
    }

    @Data @Accessors(chain = true)
    public static class Cancel {
        private Long orderId;
    }
}