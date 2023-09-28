package store.juin.api.cartitem.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.cart.model.entity.Cart;
import store.juin.api.cartitem.model.entity.CartItem;
import store.juin.api.item.model.entity.Item;

@Data
@Accessors(chain = true)
public class CartItemUpdateRequest {
    private Long itemId;

    private int count;

    public CartItem toCartItem(Long id, Cart cart, Item item, int count) {
        return CartItem.builder()
                .id(id)
                .cart(cart)
                .item(item)
                .count(count)
                .build();
    }
}
