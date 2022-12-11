package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartItem;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.request.CartItemRequest;
import com.ecommerce.backend.domain.response.CartItemResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    private final CartService cartService;
    private final ItemService itemService;


    @Transactional
    public int add(Account account, CartItemRequest.Add request) {
        final Cart cart = cartService.readByAccountId(account.getId());

        final Item item = itemService.readById(request.getItemId());

        CartItem cartItem = readByCartIdAndItemId(cart.getId(), item.getId());

        if (cartItem == null) {
            // 카트에 처음 넣는 제품이라면 새로 생성
            cartItem = request.toCartItem(cart, item, request.getCount());
            cartItemRepository.save(cartItem);
        } else {
            // 카트에 이미 있던 제품이라면 count+1
            cartItem.addCount(request.getCount());
        }

        return cartItem.getCount();
    }


    public List<CartItem> readByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.PRODUCT_NOT_FOUND_IN_CART));
    }

    public CartItem readByCartIdAndItemId(Long cartId, Long itemId) {
        return cartItemRepository.findByCartIdAndItemId(cartId, itemId);
    }

    public List<CartItemResponse.Read>
    readAllByCartIdAndItemIdListAndThumbnail(Long cartId, List<Long> itemIdList, boolean isThumbnail) {
        return cartItemRepository.findAllByCartIdAndItemIdListAndThumbnail(cartId, itemIdList, isThumbnail)
                .orElse(new ArrayList<>());
    }

    @Transactional
    public int modifyQuantity(Account account, CartItemRequest.Update request) {
        final Long itemId = request.getItemId();
        final int count = request.getCount();

        final Cart cart = cartService.readByAccountId(account.getId());

        final CartItem oldCartItem = readByCartIdAndItemId(cart.getId(), itemId);

        if (oldCartItem == null) {
            throw new EntityNotFoundException(Msg.CART_PRODUCT_NOT_FOUND);
        }

        oldCartItem.updateCount(count);
        return oldCartItem.getCount();
    }


    public long remove(Account account, CartItemRequest.Clear request) {
        final Cart cart = cartService.readByAccountId(account.getId());
        return cartItemRepository.deleteByCartIdAndItemId(cart.getId(), request.getItemId());
    }

    public int removeByAccountId(Long accountId) {
        return cartItemRepository.deleteByAccountId(accountId);
    }
}