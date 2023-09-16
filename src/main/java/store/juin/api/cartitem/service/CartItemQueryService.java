package store.juin.api.cartitem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.cartitem.model.entity.CartItem;
import store.juin.api.cartitem.model.response.CartItemResponse;
import store.juin.api.cartitem.repository.jpa.CartItemRepository;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.QueryTransactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemQueryService {
    private final QueryTransactional queryTransactional;

    private final CartItemRepository cartItemRepository;

    public List<CartItem> readByCartId(Long cartId) {
        return queryTransactional.execute(() ->
                cartItemRepository.findByCartId(cartId)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_NOT_FOUND_IN_CART))
        );
    }

    public CartItem readByCartIdAndItemId(Long cartId, Long itemId) {
        return queryTransactional.execute(() ->
                cartItemRepository.findByCartIdAndItemId(cartId, itemId)
        );
    }

    public List<CartItemResponse.Retrieve>
    readAllByCartIdAndItemIdListAndThumbnail(Long cartId, List<Long> itemIdList, boolean thumbnail, boolean representative) {
        return queryTransactional.execute(() ->
                cartItemRepository.findAllByCartIdAndItemIdListAndThumbnail(cartId, itemIdList, thumbnail, representative)
                        .orElse(new ArrayList<>())
        );
    }
}