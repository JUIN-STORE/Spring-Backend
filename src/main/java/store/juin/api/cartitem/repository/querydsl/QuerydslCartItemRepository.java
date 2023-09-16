package store.juin.api.cartitem.repository.querydsl;

import org.springframework.stereotype.Repository;
import store.juin.api.cartitem.model.entity.CartItem;
import store.juin.api.cartitem.model.response.CartItemResponse;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslCartItemRepository {
    Optional<List<CartItem>> findByCartId(Long cartId);

    long deleteByCartIdAndItemId(Long cartId, List<Long> itemIdList);

    Optional<List<CartItem>> findByCartIdAndItemIdIn(Long cartId, List<Long> itemIdList);

    Optional<List<CartItemResponse.Retrieve>>
    findAllByCartIdAndItemIdListAndThumbnail(Long cartId,
                                             List<Long> itemIdList,
                                             boolean thumbnail,
                                             boolean representative);

    long deleteByItemIdList(List<Long> itemIdList);
}