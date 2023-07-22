package store.juin.api.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Cart;
import store.juin.api.handler.CommandTransactional;
import store.juin.api.repository.jpa.CartRepository;

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
