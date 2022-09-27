package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.repository.jpa.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public void addCart(Account account){
        final Cart cart = Cart.createCart(account);
        cartRepository.save(cart);
    }

    public void remove(Account account) {
        cartRepository.deleteByAccountId(account.getId());
        cartRepository.delete(account.getCart());
    }
}
