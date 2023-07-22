package store.juin.api.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Cart;
import store.juin.api.domain.entity.CartItem;
import store.juin.api.domain.response.CartItemResponse;
import store.juin.api.exception.Msg;
import store.juin.api.handler.QueryTransactional;
import store.juin.api.repository.jpa.CartRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartQueryService {
    private final QueryTransactional queryTransactional;

    private final CartRepository cartRepository;

    private final CartItemQueryService cartItemQueryService;

    public Cart readByAccountId(Long accountId) {
        return queryTransactional.execute(() ->
                cartRepository.findByAccountId(accountId)
                            .orElseThrow(() -> new EntityNotFoundException(Msg.CART_NOT_FOUND))
        );
    }

    public Long totalItemsByAccountId(Long accountId) {
        return queryTransactional.execute(() ->
                cartRepository.countItemsByAccountId(accountId).orElse(0L)
        );
    }

    public List<CartItemResponse.Retrieve> makeCartItemRetrieveResponseList(Account account) {
        return queryTransactional.execute(() -> {
            final Cart cart = readByAccountId(account.getId());
            final List<CartItem> cartItemList = cartItemQueryService.readByCartId(cart.getId());
            final List<Long> itemIdList =
                    cartItemList.stream().map(it -> it.getItem().getId()).collect(Collectors.toList());

            return cartItemQueryService.readAllByCartIdAndItemIdListAndThumbnail(cart.getId(), itemIdList, true, true);
        });
    }

    public List<CartItemResponse.Buy> makeCartItemBuyResponseList(Account account, List<Long> itemIdList) {
        return queryTransactional.execute(() -> {
            final Cart cart = readByAccountId(account.getId());
            List<CartItemResponse.Retrieve> retrieveList
                    = cartItemQueryService.readAllByCartIdAndItemIdListAndThumbnail(cart.getId(), itemIdList, true, true);

            final List<CartItemResponse.Buy> response = new ArrayList<>();

            for (CartItemResponse.Retrieve retrieve : retrieveList) {
                CartItemResponse.Buy buy = CartItemResponse.Buy.from(retrieve);
                response.add(buy);
            }

            return response;
        });
    }
}
