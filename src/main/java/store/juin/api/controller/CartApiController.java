package store.juin.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import store.juin.api.JUINResponse;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.request.CartItemRequest;
import store.juin.api.domain.response.CartItemResponse;
import store.juin.api.service.command.CartItemCommandService;
import store.juin.api.service.query.CartQueryService;
import store.juin.api.service.query.PrincipalQueryService;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.List;

@Api(tags = {"03. Cart"})
@Slf4j
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
class CartApiController {
    private final CartQueryService cartQueryService;
    private final PrincipalQueryService principalQueryService;

    private final CartItemCommandService cartItemCommandService;

    // FIXME: 이것도 엔드포인트 애매함
    @ApiOperation(value = "카트에 항목을 추가", notes = "카트에 항목을 추가한다.")
    @PostMapping("/add")
    public JUINResponse<Integer> addItemInCart(final Principal principal,
                                               @RequestBody CartItemRequest.Add request) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][CART][CRTE]: POST /api/carts/add, identification=({}), request=({})", identification, request);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);

            var response = cartItemCommandService.add(account, request);
            return new JUINResponse<>(HttpStatus.CREATED, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][CART][CRTE]: ({}), identification=({}), request=({})", e.getMessage(), identification, request);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "카트에 있는 제품 정보 읽기", notes = "카트에 있는 제품 정보를 읽어온다.")
    @GetMapping
    public JUINResponse<List<CartItemResponse.Retrieve>> retrieveOne(final Principal principal) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][CART][ONE_]: GET /api/carts 카트 내 제품 정보 읽기, identification=({})", identification);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);

            var response = cartQueryService.makeCartItemRetrieveResponseList(account);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][CART][ONE_]: ({}), identification=({})", e.getMessage(), identification);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    // FIXME: 엔드포인트 변경해야 됨.
    @ApiOperation(value = "카트에서 buy를 클릭했을 때", notes = "주문 정보 데이터를 읽어온다.")
    @GetMapping("/buy")
    public JUINResponse<List<CartItemResponse.Buy>> buy(final Principal principal,
                                                        @RequestParam List<Long> itemIdList) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][CART][BUY_]: GET /api/carts/buy, identification=({}), itemList=({})", identification, itemIdList);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);

            var response = cartQueryService.makeCartItemBuyResponseList(account, itemIdList);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][CART][BUY_]: ({}), identification=({})", e.getMessage(), identification);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    // FIXME: 프론트에서 state으로 관리해야 됨.
    @ApiOperation(value = "카트에 담긴 상품 개수 변경.", notes = "카트에 개수를 변경한다.")
    @PutMapping("/quantity")
    public JUINResponse<Integer> updateQuantity(final Principal principal,
                                                @RequestBody CartItemRequest.Update request) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][CART][UPDT]: PUT /api/carts/quantity, identification=({}), request=({})"
                , identification, request);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);

            var response = cartItemCommandService.modifyQuantity(account, request);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][CART][UPDT]: ({}), identification=({}), request=({})", e.getMessage(), identification, request);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "카트에 추가된 상품을 제거", notes = "카트에 추가된 상품을 제거한다.")
    @DeleteMapping
    public JUINResponse<Long> deleteItemInCart(final Principal principal,
                                               @RequestParam List<Long> itemIdList) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][CART][DEL_]: DELETE /api/carts/clear, identification=({}), itemIdList=({})"
                , identification, itemIdList);

        try {
            final Account account = principalQueryService.readByPrincipal(principal);

            var response = cartItemCommandService.remove(account, itemIdList);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P5][CTRL][CART][DEL_]: ({}), identification=({}), request=({})", e.getMessage(), identification, itemIdList);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST);
        }
    }
}