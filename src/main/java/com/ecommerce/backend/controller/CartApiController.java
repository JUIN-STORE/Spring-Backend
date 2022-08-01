package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.request.CartProductRequest;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.ecommerce.backend.service.CartProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
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

@Api(tags = {"04. Cart"})
@Slf4j
@RestController
@RequestMapping("/api/carts")
@AllArgsConstructor
public class CartApiController {
    private CartProductService cartProductService;

    @ApiOperation(value = "카트 추가", notes="카트에 추가한다.")
    @PostMapping("/add")
    public MyResponse<CartProductResponse.Add> addCart(@RequestBody CartProductRequest.Add request, Principal principal) {
        final CartProductResponse.Add response = cartProductService.addCart(request, principal);
        return new MyResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카트 제거", notes="카트에서 제거한다.")
    @DeleteMapping("/clear")
    public MyResponse<Integer> clearCart(@RequestBody CartProductRequest.Clear request, Principal principal) {
        final int deleteCount = cartProductService.deleteCart(request, principal);
        return new MyResponse<>(HttpStatus.OK, deleteCount);
    }
}