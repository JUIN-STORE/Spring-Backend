package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.enums.ProductStatus;
import lombok.Data;
import lombok.experimental.Accessors;

public class ProductRequest {
    @Data @Accessors(chain = true)
    public static class Create {
        private Long categoryId;

        private String productName;

        private Integer price;          // 제품의 가격

        private Integer quantity;       // 제품의 총 개수

        private String description;

        public Product toProduct(Category category) {
            return Product.builder()
                    .category(category)
                    .productName(this.productName)
                    .price(this.price)
                    .quantity(this.quantity)
                    .description(this.description)
                    .productStatus(ProductStatus.READY)
                    .soldCount(0)
                    .build();
        }
    }
}