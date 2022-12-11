package com.ecommerce.backend.service.relation;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartItem;
import com.ecommerce.backend.domain.response.CartItemResponse;
import com.ecommerce.backend.service.CartItemService;
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
    private final CartService cartService;
    private final CartItemService cartItemService;

    public List<CartItemResponse.Read> makeCartItemReadResponse(Account account) {
        final Cart cart = cartService.readByAccountId(account.getId());

        final List<CartItem> cartItemList = cartItemService.readByCartId(cart.getId());
        final List<Long> itemIdList =
                cartItemList.stream().map(cp -> cp.getItem().getId()).collect(Collectors.toList());

        return cartItemService.readAllByCartIdAndItemIdListAndThumbnail(cart.getId(), itemIdList, true);
    }

    public List<CartItemResponse.Buy> makeCartItemBuyResponse(Account account, List<Long> itemIdList) {
        final Cart cart = cartService.readByAccountId(account.getId());
        List<CartItemResponse.Read> readList
                = cartItemService.readAllByCartIdAndItemIdListAndThumbnail(cart.getId(), itemIdList, true);

        final List<CartItemResponse.Buy> response = new ArrayList<>();

        for (CartItemResponse.Read read : readList) {
            CartItemResponse.Buy buy = CartItemResponse.Buy.from(read);
            response.add(buy);
        }

        return response;
    }
}