package com.ecommerce.backend.service.relation;

import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.response.OrderResponse;
import com.ecommerce.backend.service.OrderItemService;
import com.ecommerce.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRelationService {
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public OrderResponse.Delete remove(Long accountId) {
        final List<Order> orderList = orderService.readByAccountId(accountId);
        final List<Long> orderIdList = orderList.stream().map(Order::getId).collect(Collectors.toList());
        long orderItemDeleteCount = orderItemService.removeByOrderIdList(orderIdList);
        long ordersDeleteCount = orderService.removeByAccountId(accountId);

        return OrderResponse.Delete.of(ordersDeleteCount, orderItemDeleteCount);
    }
}
