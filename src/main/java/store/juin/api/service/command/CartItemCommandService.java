package store.juin.api.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Cart;
import store.juin.api.domain.entity.CartItem;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.request.CartItemRequest;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.CartItemRepository;
import store.juin.api.service.query.CartItemQueryService;
import store.juin.api.service.query.CartQueryService;
import store.juin.api.service.query.ItemQueryService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemCommandService {
    private final CartItemRepository cartItemRepository;

    private final CartQueryService cartQueryService;
    private final ItemQueryService itemQueryService;
    private final CartItemQueryService cartItemQueryService;

    @Transactional
    public int add(Account account, CartItemRequest.Add request) {
        final Cart cart = cartQueryService.readByAccountId(account.getId());

        final Item item = itemQueryService.readById(request.getItemId());

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
    }

    @Transactional
    public int modifyQuantity(Account account, CartItemRequest.Update request) {
        final Long itemId = request.getItemId();
        final int count = request.getCount();

        final Cart cart = cartQueryService.readByAccountId(account.getId());

        final CartItem oldCartItem = cartItemQueryService.readByCartIdAndItemId(cart.getId(), itemId);

        if (oldCartItem == null) {
            throw new EntityNotFoundException(Msg.CART_ITEM_NOT_FOUND);
        }

        oldCartItem.updateCount(count);
        return oldCartItem.getCount();
    }


    public long remove(Account account, List<Long> itemIdList) {
        final Cart cart = cartQueryService.readByAccountId(account.getId());
        return cartItemRepository.deleteByCartIdAndItemId(cart.getId(), itemIdList);
    }

    public int removeByAccountId(Long accountId) {
        return cartItemRepository.deleteByAccountId(accountId);
    }

    public long removeByItemIdList(List<Long> itemIdList) {
        return cartItemRepository.deleteByItemIdList(itemIdList);
    }
}