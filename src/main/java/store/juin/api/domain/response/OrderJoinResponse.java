package store.juin.api.domain.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.domain.enums.OrderStatus;

import java.time.LocalDateTime;

@Data @Accessors(chain = true)
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

    private String itemImageName;

    private String originImageName;

    private String imageUrl;
}