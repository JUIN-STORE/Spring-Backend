//package com.ecommerce.backend.relation;
//
//import com.ecommerce.backend.domain.entity.Order;
//import com.ecommerce.backend.domain.response.OrderResponse;
//import com.ecommerce.backend.service.command.OrderCommandService;
//import com.ecommerce.backend.service.command.OrderItemCommandService;
//import com.ecommerce.backend.service.query.OrderQueryService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class OrderRelationService {
//    private final OrderQueryService orderQueryService;
//    private final OrderCommandService orderCommandService;
//
//    private final OrderItemCommandService orderItemQueryService;
//
//    public OrderResponse.Delete remove(Long accountId) {
//        final List<Order> orderList = orderQueryService.readAllByAccountId(accountId);
//        final List<Long> orderIdList = orderList.stream().map(Order::getId).collect(Collectors.toList());
//        long orderItemDeleteCount = orderItemQueryService.removeByOrderIdList(orderIdList);
//        long ordersDeleteCount = orderCommandService.removeByAccountId(accountId);
//
//        return OrderResponse.Delete.of(ordersDeleteCount, orderItemDeleteCount);
//    }
//}
