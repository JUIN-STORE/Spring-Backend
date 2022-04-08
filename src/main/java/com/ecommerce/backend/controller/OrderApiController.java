package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.service.OrderService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Api(tags = {"03. Order"})
@Slf4j
@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
public class OrderApiController {
    private final OrderService orderService;

    @PostMapping("/")
    public MyResponse<Long> test(@RequestBody OrderRequest.OrderCreate request,  Principal principal) {
        Long order = orderService.order(request, principal.getName());
        System.out.println("order = " + order);
        return null;
    }
}