package store.juin.api.cart.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.account.model.entity.Account;
import store.juin.api.cart.model.entity.Cart;
import store.juin.api.cart.repository.jpa.CartRepository;
import store.juin.api.common.handler.CommandTransactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartCommandService {
    private final CommandTransactional commandTransactional;

    private final CartRepository cartRepository;

    public void add(Account account) {
        final Cart cart = Cart.createCart(account);

        commandTransactional.execute(() ->
                cartRepository.save(cart)
        );
    }

    public long removeByAccountId(Long accountId) {
        return commandTransactional.execute(() ->
                cartRepository.deleteByAccountId(accountId)
        );
    }
}
