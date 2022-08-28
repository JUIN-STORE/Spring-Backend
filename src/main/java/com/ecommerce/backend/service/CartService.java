package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service Naming
 * C -> add
 * R -> findBy~
 * U -> update
 * D -> delete
 */

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
