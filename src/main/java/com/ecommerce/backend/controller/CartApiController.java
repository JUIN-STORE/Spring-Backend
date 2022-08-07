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
import java.util.List;

/** Controller Naming
 * C -> create
 * R -> read
 * U -> modify
 * D -> remove
 */

@Api(tags = {"04. Cart"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/carts")
public class CartApiController {
    private final CartProductService cartProductService;

    @ApiOperation(value = "카트에 있는 프로덕트 정보 읽기", notes="카트에 있는 프로덕트 정보를 읽어온다.")
    @GetMapping
    public MyResponse<List<CartProductResponse.Read>> readCart(Principal principal) {
        final List<CartProductResponse.Read> response = cartProductService.readCart(principal);
        return new MyResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카트에 항목을 추가", notes="카트에 항목을 추가한다.")
    @PostMapping("/add")
    public MyResponse<CartProductResponse.Add> addCart(@RequestBody CartProductRequest.Add request, Principal principal) {
        final CartProductResponse.Add response = cartProductService.addCart(request, principal);
        return new MyResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카트 개수 변경.", notes="카트에 개수를 변경한다.")
    @PutMapping("/quantity")
    public MyResponse<Integer> updateQuantity(@RequestBody CartProductRequest.Update request, Principal principal) {
        final int response = cartProductService.updateQuantity(request, principal);
        return new MyResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카트 제거", notes="카트에서 제거한다.")
    @DeleteMapping("/clear")
    public MyResponse<Integer> clearCart(@RequestBody CartProductRequest.Clear request, Principal principal) {
        final int deleteCount = cartProductService.deleteCart(request, principal);
        return new MyResponse<>(HttpStatus.OK, deleteCount);
    }
}