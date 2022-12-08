package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public void add(Account account) {
        final Cart cart = Cart.createCart(account);

        cartRepository.save(cart);
    }


    public Cart readByAccountId(Long accountId) {
        return cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.CART_NOT_FOUND));
    }


    public long removeByAccountId(Long accountId) {
        return cartRepository.deleteByAccountId(accountId);
    }
}
