package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Product;
import lombok.Data;
import lombok.experimental.Accessors;

public class ProductRequest {
    @Data @Accessors(chain = true)
    public static class CreateRequest {
        private String productName;

        private Integer price;          // 제품의 가격

        private Integer quantity;       // 제품의 총 개수

        private String description;

        public Product toProduct() {
            return Product.builder()
                    .productName(this.productName)
                    .price(this.price)
                    .quantity(this.quantity)
                    .description(this.description)
                    .build();
        }
    }
}