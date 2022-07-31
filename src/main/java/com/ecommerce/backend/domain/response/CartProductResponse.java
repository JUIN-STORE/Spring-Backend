package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.CartProduct;
import lombok.Data;
import lombok.experimental.Accessors;

public class CartProductResponse {
    @Data @Accessors(chain = true)
    public static class Add {
        private Long cartId;

        private Long productId;

        public static Add fromCartProduct(CartProduct cartProduct) {
            return new Add()
                    .setCartId(cartProduct.getCart().getId())
                    .setProductId(cartProduct.getProduct().getId());
        }
    }
}
