package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.enums.OrderStatus;

import java.time.LocalDateTime;

public interface OrderJoinResult {
    Long getOrdersId();

    Long getOrderProductId();

    Long getProductId();

    Long getDeliveryId();

    Integer getOrderCount();

    int getPrice();

    String getProductName();

    LocalDateTime getOrderDate();

    OrderStatus getOrderStatus();
}
