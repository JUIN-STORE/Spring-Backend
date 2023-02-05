package store.juin.api.domain.response;

import store.juin.api.domain.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderJoinResponse {
    private Long ordersId;

    private Long orderItemId;

    private Long itemId;

    private Long deliveryId;

    private Integer orderCount;

    private int price;

    private String name;

    private LocalDateTime orderDate;

    private OrderStatus orderStatus;
}