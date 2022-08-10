package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.domain.response.OrderResponse;
import com.ecommerce.backend.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/** Controller Naming
 * C -> create
 * R -> read
 * U -> modify
 * D -> remove
 */

@Api(tags = {"05. Order"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApiController {
    private final OrderService orderService;

    @ApiOperation(value = "주문하기", notes="주문을 한다.")
    @PostMapping("/new")
    public MyResponse<OrderResponse.Create> create(@RequestBody OrderRequest.Create request, Principal principal) {
        final Order order = orderService.newOrder(request, principal.getName());
        return new MyResponse<>(HttpStatus.OK, OrderResponse.Create.of(order));
    }

    @ApiOperation(value = "주문 취소하기", notes="주문 취소를 한다.")
    @DeleteMapping("/cancel/{orderId}")
    public MyResponse<OrderResponse.Create> cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return new MyResponse<>(HttpStatus.OK, "POST SUCCESS", null);
    }
}