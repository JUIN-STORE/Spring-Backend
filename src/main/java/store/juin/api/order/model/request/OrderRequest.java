package store.juin.api.order.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import store.juin.api.address.model.request.AddressRequest;
import store.juin.api.delivery.model.request.DeliveryRequest;
import store.juin.api.order.enumeration.OrderStatus;
import store.juin.api.order.model.entity.Order;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
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