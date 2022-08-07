package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.domain.entity.Product;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

public class CartProductRequest {
    @Data @Accessors(chain = true)
    public static class Read implements Serializable{
        private Long productId;

        private int count;

        public CartProduct toCartProduct() {
            return CartProduct.builder()
                    .product(Product.builder().id(productId).build())
                    .count(count)
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class Add implements Serializable{
        private Long productId;

        private int count;

        public CartProduct toCartProduct() {
            return CartProduct.builder()
                    .product(Product.builder().id(productId).build())
                    .count(count)
                    .build();
        }
    }

    @Data @Accessors(chain = true)
    public static class Update implements Serializable{
        private Long productId;

        private int count;
    }


    @Data @Accessors(chain = true)
    public static class Clear implements Serializable{
        private Long productId;

        public CartProduct toCartProduct() {
            return CartProduct.builder()
                    .product(Product.builder().id(productId).build())
                    .count(0)
                    .build();
        }
    }
}