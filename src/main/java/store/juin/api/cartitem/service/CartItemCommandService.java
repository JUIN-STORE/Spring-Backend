package store.juin.api.cartitem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.account.model.entity.Account;
import store.juin.api.cart.model.entity.Cart;
import store.juin.api.cart.service.query.CartQueryService;
import store.juin.api.cartitem.model.entity.CartItem;
import store.juin.api.cartitem.model.request.CartItemAddRequest;
import store.juin.api.cartitem.model.request.CartItemUpdateRequest;
import store.juin.api.cartitem.repository.jpa.CartItemRepository;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.CommandTransactional;
import store.juin.api.item.model.entity.Item;
import store.juin.api.item.service.query.ItemQueryService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemCommandService {
    private final CommandTransactional commandTransactional;

    private final CartItemRepository cartItemRepository;

    private final CartQueryService cartQueryService;
    private final ItemQueryService itemQueryService;
    private final CartItemQueryService cartItemQueryService;


    public int add(Account account, CartItemAddRequest request) {
        final Cart cart = cartQueryService.readByAccountId(account.getId());

        final Item item = itemQueryService.readById(request.getItemId());

        return commandTransactional.execute(() -> {
            CartItem cartItem = cartItemQueryService.readByCartIdAndItemId(cart.getId(), item.getId());
            if (cartItem == null) {
                // 카트에 처음 넣는 제품이라면 새로 생성
                cartItem = request.toCartItem(cart, item, request.getCount());
                cartItemRepository.save(cartItem);
            } else {
                // 카트에 이미 있던 제품이라면 count+1
                cartItem.addCount(request.getCount());
            }
            return cartItem.getCount();
        });
    }

    public int modifyQuantity(Account account, CartItemUpdateRequest request) {
        final Long itemId = request.getItemId();
        final int count = request.getCount();

        return commandTransactional.execute(() -> {
            final Cart cart = cartQueryService.readByAccountId(account.getId());

            final CartItem oldCartItem = cartItemQueryService.readByCartIdAndItemId(cart.getId(), itemId);

            if (oldCartItem == null) {
                throw new EntityNotFoundException(Msg.CART_ITEM_NOT_FOUND);
            }

            oldCartItem.updateCount(count);

            return oldCartItem.getCount();
        });
    }


    public long remove(Account account, List<Long> itemIdList) {
        return commandTransactional.execute(() -> {
            final Cart cart = cartQueryService.readByAccountId(account.getId());

            return cartItemRepository.deleteByCartIdAndItemId(cart.getId(), itemIdList);
        });
    }

    public int removeByAccountId(Long accountId) {
        return commandTransactional.execute(() ->
                cartItemRepository.deleteByAccountId(accountId)
        );
    }

    public long removeByItemIdList(List<Long> itemIdList) {
        return commandTransactional.execute(() ->
                cartItemRepository.deleteByItemIdList(itemIdList)
        );
    }
}