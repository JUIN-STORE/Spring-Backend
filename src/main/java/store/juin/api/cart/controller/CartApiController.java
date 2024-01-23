package store.juin.api.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import store.juin.api.account.model.entity.Account;
import store.juin.api.cart.service.query.CartQueryService;
import store.juin.api.cartitem.model.request.CartItemAddRequest;
import store.juin.api.cartitem.model.request.CartItemUpdateRequest;
import store.juin.api.cartitem.model.response.CartItemResponse;
import store.juin.api.cartitem.service.CartItemCommandService;
import store.juin.api.common.model.response.JUINResponse;
import store.juin.api.principal.service.query.PrincipalQueryService;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartApiController {
    private final CartQueryService cartQueryService;
    private final PrincipalQueryService principalQueryService;

    private final CartItemCommandService cartItemCommandService;

    @PostMapping("/add")
    public JUINResponse<Integer> addItemInCart(final Principal principal,
                                               @RequestBody CartItemAddRequest request) {
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

    @GetMapping("/count")
    public JUINResponse<Long> count(final Principal principal) {
        final String identification = principal.getName();
        log.info("[P9][CTRL][CART][CONT]: GET /api/carts/count 카트 개수, identification=({})", identification);

        final Account account = principalQueryService.readByPrincipal(principal);

        var response = cartQueryService.totalItemsByAccountId(account.getId());
        return new JUINResponse<>(HttpStatus.OK, response);
    }

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

    @PutMapping("/quantity")
    public JUINResponse<Integer> updateQuantity(final Principal principal,
                                                @RequestBody CartItemUpdateRequest request) {
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