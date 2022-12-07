package com.ecommerce.backend.service.relation;

import com.ecommerce.backend.domain.entity.*;
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

        final List<Product> productList = productRelationService.getProductList(productIdList);
        final int size = productList.size();

        final List<CartProduct> cartProductListInCart = cartProductService.readByCartId(cart.getId());
        final List<ProductImage> productImageList = productRelationService.getThumbnailProductImageList(true);

        final List<CartProductResponse.Buy> response = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            response.add(
                    CartProductResponse.Buy.from(
                            productList.get(i),
                            productImageList.get(i),
                            cartProductListInCart.get(i).getCount()
                    )
            );
        }

        return response;
    }
}