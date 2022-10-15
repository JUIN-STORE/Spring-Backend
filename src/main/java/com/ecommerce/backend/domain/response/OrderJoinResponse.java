package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderJoinResponse {
    private Long ordersId;

    private Long orderProductId;

    private Long productId;

    private Long deliveryId;

    private Integer orderCount;

    private int price;

    private String productName;

    private LocalDateTime orderDate;

    private OrderStatus orderStatus;
}