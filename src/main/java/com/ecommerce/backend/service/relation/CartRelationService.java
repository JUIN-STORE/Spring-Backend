package com.ecommerce.backend.service.relation;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.ecommerce.backend.service.CartProductService;
import com.ecommerce.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartRelationService {
    private final ProductRelationService productRelationService;

    private final CartService cartService;
    private final CartProductService cartProductService;

    public List<CartProductResponse.Read> makeCartProductReadResponse(Account account) {
        final Cart cart = cartService.readByAccountId(account.getId());

        final List<CartProduct> cartProductList = cartProductService.readByCartId(cart.getId());
        final List<Long> productIdList =
                cartProductList.stream().map(cp -> cp.getProduct().getId()).collect(Collectors.toList());

        return cartProductService.readAllByCartIdAndProductIdListAndThumbnail(cart.getId(), productIdList, true);
    }

    public List<CartProductResponse.Buy> makeCartProductBuyResponse(Account account, List<Long> productIdList) {
        final Cart cart = cartService.readByAccountId(account.getId());
        List<CartProductResponse.Read> reads = cartProductService.readAllByCartIdAndProductIdListAndThumbnail(cart.getId(), productIdList, true);

        final List<CartProductResponse.Buy> response = new ArrayList<>();

        for (CartProductResponse.Read read : reads) {
            CartProductResponse.Buy buy = CartProductResponse.Buy.from(read);
            response.add(buy);
        }

        return response;
    }
}