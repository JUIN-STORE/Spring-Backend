package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.domain.response.OrderJoinResponse;
import com.ecommerce.backend.domain.response.OrderResponse;
import com.ecommerce.backend.service.JwtService;
import com.ecommerce.backend.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Api(tags = {"04. Order"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApiController {
    private final JwtService jwtService;

    private final OrderService orderService;

    @ApiOperation(value = "주문 상세보기", notes="주문 취소를 한다.")
    @GetMapping
    public MyResponse<List<OrderJoinResponse>> all(Principal principal) {
        final Account account = jwtService.readByPrincipal(principal);

        final List<OrderJoinResponse> response = orderService.join(account);
        return new MyResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "주문하기", notes="주문을 한다.")
    @PostMapping("/new")
    public MyResponse<OrderResponse.Create> newOrder(Principal principal,
                                                     @RequestBody OrderRequest.Create request) {
        final Account account = jwtService.readByPrincipal(principal);

        final Order order = orderService.addOrder(account, request);
        return new MyResponse<>(HttpStatus.OK, OrderResponse.Create.of(order));
    }

    @ApiOperation(value = "주문 취소하기", notes="주문 취소를 한다.")
    @DeleteMapping("/cancel/{orderId}")
    public MyResponse<OrderResponse.Create> cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return new MyResponse<>(HttpStatus.OK, null);
    }
}