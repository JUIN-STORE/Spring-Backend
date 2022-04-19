package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.domain.response.OrderResponse;
import com.ecommerce.backend.service.OrderService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/** Controller Naming
 * C -> create
 * R -> read
 * U -> modify
 * D -> remove
 */

@Api(tags = {"04. Order"})
@Slf4j
@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderApiController {
    private final OrderService orderService;

    @PostMapping("/new-order")
    public MyResponse<OrderResponse.CreateResponse> create (@RequestBody OrderRequest.CreateRequest request, Principal principal) {
        final Order order = orderService.order(request, principal.getName());
        return new MyResponse<>(HttpStatus.OK, "POST SUCCESS", OrderResponse.CreateResponse.fromOrder(order));
    }
}