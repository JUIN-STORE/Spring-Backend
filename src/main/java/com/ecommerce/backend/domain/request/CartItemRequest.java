package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartItem;
import com.ecommerce.backend.domain.entity.Item;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

public class CartItemRequest {
    @Data @Accessors(chain = true)
    public static class Read implements Serializable{
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
    public static class Add implements Serializable{
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
    public static class Update implements Serializable {
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
    public static class Clear implements Serializable{
        private Long itemId;

        public CartItem toCartItem() {
            return CartItem.builder()
                    .item(Item.builder().id(itemId).build())
                    .count(0)
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class Buy implements Serializable {
        private List<Long> itemIdList;
    }
}