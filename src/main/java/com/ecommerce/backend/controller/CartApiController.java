package com.ecommerce.backend.controller;

import com.ecommerce.backend.MyResponse;
import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.request.CartProductRequest;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.ecommerce.backend.service.CartProductService;
import com.ecommerce.backend.service.JwtService;
import com.ecommerce.backend.service.relation.CartRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Api(tags = {"03. Cart"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/carts")
class CartApiController {
    private final JwtService jwtService;

    private final CartRelationService cartRelationService;

    private final CartProductService cartProductService;

    @ApiOperation(value = "카트에 있는 제품 정보 읽기", notes = "카트에 있는 제품 정보를 읽어온다.")
    @GetMapping
    public MyResponse<List<CartProductResponse.Read>> one(final Principal principal) {

        final Account account = jwtService.readByPrincipal(principal);

        var response = cartRelationService.makeCartProductReadResponse(account);
        return new MyResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카트에 항목을 추가", notes = "카트에 항목을 추가한다.")
    @PostMapping("/add")
    public MyResponse<Integer> newCartItem(final Principal principal,
                                           @RequestBody CartProductRequest.Add request) {


        final Account account = jwtService.readByPrincipal(principal);
        var response = cartProductService.add(account, request);

        return new MyResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카트 개수 변경.", notes = "카트에 개수를 변경한다.")
    @PutMapping("/quantity")
    public MyResponse<Integer> updateQuantity(final Principal principal,
                                              @RequestBody CartProductRequest.Update request) {

        final Account account = jwtService.readByPrincipal(principal);

        var response = cartProductService.modifyQuantity(account, request);
        return new MyResponse<>(HttpStatus.OK, response);
    }

    // FIXME: 엔드포인트가 직관적이지 않아서 변경해야 되는데 어떤 게 좋을지;;
    @ApiOperation(value = "카트에 추가된 상품을 제거", notes = "카트에 추가된 상품을 제거한다.")
    @DeleteMapping("/clear")
    public MyResponse<Long> clearCart(final Principal principal,
                                      @RequestBody CartProductRequest.Clear request) {

        final Account account = jwtService.readByPrincipal(principal);

        var response = cartProductService.remove(account, request);
        return new MyResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카트에셔 buy를 클릭했을 때", notes = "주문 정보 데이터를 읽어온다.")
    @GetMapping("/buy")
    public MyResponse<List<CartProductResponse.Buy>> buy(final Principal principal,
                                                         @RequestParam List<Long> productList) {

        final Account account = jwtService.readByPrincipal(principal);

        var response = cartRelationService.makeCartProductBuyResponse(account, productList);
        return new MyResponse<>(HttpStatus.OK, response);
    }
}