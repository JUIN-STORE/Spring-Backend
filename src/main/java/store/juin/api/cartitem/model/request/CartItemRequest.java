package store.juin.api.cartitem.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.cart.model.entity.Cart;
import store.juin.api.cartitem.model.entity.CartItem;
import store.juin.api.item.model.entity.Item;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CartItemRequest {
    @Data @Accessors(chain = true)
    public static class Retrieve implements Serializable{
        private Long itemId;

        private int count;

        public CartItem toCartItem() {
            return CartItem.builder()
                    .item(Item.builder().id(itemId).build())
                    .count(count)
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class Add {
        private Long itemId;

        private int count;

        public CartItem toCartItem(Cart cart, Item item, int count) {
            return CartItem.builder()
                    .cart(cart)
                    .item(item)
                    .count(count)
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class Update {
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


    @Data @Accessors(chain = true)
    public static class Clear {
        private Long itemId;

        public CartItem toCartItem() {
            return CartItem.builder()
                    .item(Item.builder().id(itemId).build())
                    .count(0)
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class Buy {
        private List<Long> itemIdList;
    }
}