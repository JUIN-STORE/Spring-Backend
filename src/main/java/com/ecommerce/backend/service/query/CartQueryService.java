package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartItem;
import com.ecommerce.backend.domain.response.CartItemResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartQueryService {
    private final CartRepository cartRepository;

    private final CartItemQueryService cartItemQueryService;

    @Transactional(readOnly = true)
    public Cart readByAccountId(Long accountId) {
        return cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.CART_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse.Read> makeCartItemReadResponse(Account account) {
        final Cart cart = readByAccountId(account.getId());

        final List<CartItem> cartItemList = cartItemQueryService.readByCartId(cart.getId());
        final List<Long> itemIdList =
                cartItemList.stream().map(cp -> cp.getItem().getId()).collect(Collectors.toList());

        return cartItemQueryService.readAllByCartIdAndItemIdListAndThumbnail(cart.getId(), itemIdList, true);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse.Buy> makeCartItemBuyResponse(Account account, List<Long> itemIdList) {
        final Cart cart = readByAccountId(account.getId());
        List<CartItemResponse.Read> readList
                = cartItemQueryService.readAllByCartIdAndItemIdListAndThumbnail(cart.getId(), itemIdList, true);

        final List<CartItemResponse.Buy> response = new ArrayList<>();

        for (CartItemResponse.Read read : readList) {
            CartItemResponse.Buy buy = CartItemResponse.Buy.from(read);
            response.add(buy);
        }

        return response;
    }
}
