package store.juin.api.cartitem.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.cartitem.model.entity.CartItem;
import store.juin.api.item.model.entity.Item;

@Data
@Accessors(chain = true)
public class CartItemClearRequest {
    private Long itemId;

    public CartItem toCartItem() {
        return CartItem.builder()
                .item(Item.builder().id(itemId).build())
                .count(0)
                .build();
    }
}