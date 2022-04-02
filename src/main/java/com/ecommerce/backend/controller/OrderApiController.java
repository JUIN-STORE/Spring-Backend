package com.ecommerce.backend.controller;

import com.ecommerce.backend.service.OrderService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private final String ERROR_MESSAGE = "ERROR";


//    @PostMapping("/")
//    public MyResponse<Long> create(@RequestBody CreateRequest request, Principal principal) {
//        String email = principal.getName();
//        System.out.println("email = " + email);
//        Long orderId;
//        try {
//            orderId = orderService.order(request, email);
//        } catch (Exception e){
//            return new MyResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
//        }
//        return new MyResponse<>(HttpStatus.OK, "SUCCESS", orderId);
//    }
}