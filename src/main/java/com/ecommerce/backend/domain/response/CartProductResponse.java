package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.CartProduct;
import lombok.Data;
import lombok.experimental.Accessors;

public class CartProductResponse {
    @Data @Accessors(chain = true)
    public static class Add {
        private Long productId;

        private int count;

        public static Add fromCartProduct(CartProduct cartProduct) {
            return new Add()
                    .setProductId(cartProduct.getProduct().getId())
                    .setCount(cartProduct.getCount());
        }
    }
}
