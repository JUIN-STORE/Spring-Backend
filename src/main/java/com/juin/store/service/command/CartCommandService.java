package com.juin.store.service.command;

import com.juin.store.domain.entity.Account;
import com.juin.store.domain.entity.Cart;
import com.juin.store.repository.jpa.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartCommandService {
    private final CartRepository cartRepository;

    public void add(Account account) {
        final Cart cart = Cart.createCart(account);

        cartRepository.save(cart);
    }

    public long removeByAccountId(Long accountId) {
        return cartRepository.deleteByAccountId(accountId);
    }
}
