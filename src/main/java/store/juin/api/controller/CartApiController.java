package store.juin.api.controller;

import store.juin.api.JUINResponse;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.request.CartItemRequest;
import store.juin.api.domain.response.CartItemResponse;
import store.juin.api.service.command.CartItemCommandService;
import store.juin.api.service.query.CartQueryService;
import store.juin.api.service.query.PrincipalQueryService;
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
    private final CartQueryService cartQueryService;
    private final PrincipalQueryService principalQueryService;

    private final CartItemCommandService cartItemCommandService;

    @ApiOperation(value = "카트에 있는 제품 정보 읽기", notes = "카트에 있는 제품 정보를 읽어온다.")
    @GetMapping
    public JUINResponse<List<CartItemResponse.Retrieve>> retrieveOne(final Principal principal) {

        final Account account = principalQueryService.readByPrincipal(principal);
        log.info("[P9][CTRL][CART][NEW_]: GET /api/carts 카트 내 제품 정보 읽기, email=({})", account.getEmail());

        var response = cartQueryService.makeCartItemReadResponse(account);
        return new JUINResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카트에 항목을 추가", notes = "카트에 항목을 추가한다.")
    @PostMapping("/add")
    public JUINResponse<Integer> create(final Principal principal,
                                        @RequestBody CartItemRequest.Add request) {
        final Account account = principalQueryService.readByPrincipal(principal);
        log.info("[P9][CTRL][CART][NEW_]: POST /api/carts/add, email=({}), request=({})", account.getEmail(), request);

        var response = cartItemCommandService.add(account, request);
        return new JUINResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카트에 담긴 상품 개수 변경.", notes = "카트에 개수를 변경한다.")
    @PutMapping("/quantity")
    public JUINResponse<Integer> updateQuantity(final Principal principal,
                                                @RequestBody CartItemRequest.Update request) {

        final Account account = principalQueryService.readByPrincipal(principal);
        log.info("[P9][CTRL][CART][UPDT]: PUT /api/carts/quantity, email=({}), request=({})", account.getEmail(), request);

        var response = cartItemCommandService.modifyQuantity(account, request);
        return new JUINResponse<>(HttpStatus.OK, response);
    }

    // FIXME: 엔드포인트가 직관적이지 않아서 변경해야 되는데 어떤 게 좋을지;;
    @ApiOperation(value = "카트에 추가된 상품을 제거", notes = "카트에 추가된 상품을 제거한다.")
    @DeleteMapping("/clear")
    public JUINResponse<Long> clearCart(final Principal principal,
                                        @RequestBody CartItemRequest.Clear request) {

        final Account account = principalQueryService.readByPrincipal(principal);
        log.info("[P9][CTRL][CART][DEL_]: DELETE /api/carts/clear, email=({}), request=({})", account.getEmail(), request);

        var response = cartItemCommandService.remove(account, request);
        return new JUINResponse<>(HttpStatus.OK, response);
    }

    @ApiOperation(value = "카트에셔 buy를 클릭했을 때", notes = "주문 정보 데이터를 읽어온다.")
    @GetMapping("/buy")
    public JUINResponse<List<CartItemResponse.Buy>> buy(final Principal principal,
                                                        @RequestParam List<Long> itemList) {

        final Account account = principalQueryService.readByPrincipal(principal);
        log.info("[P9][CTRL][CART][BUY_]: GET /api/carts/buy, email=({}), itemList=({})", account.getEmail(), itemList);

        var response = cartQueryService.makeCartItemBuyResponse(account, itemList);
        return new JUINResponse<>(HttpStatus.OK, response);
    }
}