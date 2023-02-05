package com.juin.store.service.query;

import com.juin.store.domain.entity.Account;
import com.juin.store.domain.entity.Cart;
import com.juin.store.domain.entity.CartItem;
import com.juin.store.domain.response.CartItemResponse;
import com.juin.store.exception.Msg;
import com.juin.store.repository.jpa.CartRepository;
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
    public List<CartItemResponse.Retrieve> makeCartItemReadResponse(Account account) {
        final Cart cart = readByAccountId(account.getId());

        final List<CartItem> cartItemList = cartItemQueryService.readByCartId(cart.getId());
        final List<Long> itemIdList =
                cartItemList.stream().map(cp -> cp.getItem().getId()).collect(Collectors.toList());

        return cartItemQueryService.readAllByCartIdAndItemIdListAndThumbnail(cart.getId(), itemIdList, true);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse.Buy> makeCartItemBuyResponse(Account account, List<Long> itemIdList) {
        final Cart cart = readByAccountId(account.getId());
        List<CartItemResponse.Retrieve> retrieveList
                = cartItemQueryService.readAllByCartIdAndItemIdListAndThumbnail(cart.getId(), itemIdList, true);

        final List<CartItemResponse.Buy> response = new ArrayList<>();

        for (CartItemResponse.Retrieve retrieve : retrieveList) {
            CartItemResponse.Buy buy = CartItemResponse.Buy.from(retrieve);
            response.add(buy);
        }

        return response;
    }
}
