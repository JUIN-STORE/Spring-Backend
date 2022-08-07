package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.domain.entity.Product;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartProductResponse {
    @Data @Accessors(chain = true)
    public static class Read {
        private Long productId;

        private String productName;             // 제품의 이름

        private Integer price;                  // 제품의 가격

        private Integer count;               // 제품의 총 개수

        private String description;             // 제품 설명

        private List<ProductImageResponse.Read> ProductImageList = new ArrayList<>();

        public static CartProductResponse.Read fromCartProduct(Product product, int count) {
            return new CartProductResponse.Read()
                    .setProductId(product.getId())
                    .setProductName(product.getProductName())
                    .setPrice(product.getPrice())
                    .setCount(count)
                    .setDescription(product.getDescription())
                    .setProductImageList(product.getProductImageList().stream() // 이 시점에 쿼리 나감
                            .map(ProductImageResponse.Read::fromProduct)
                            .collect(Collectors.toList()));
        }
    }


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

    @Data @Accessors(chain = true)
    public static class Remove {
        private Long productId;

        private int count;

        public static Add fromCartProduct(CartProduct cartProduct) {
            return new Add()
                    .setProductId(cartProduct.getProduct().getId())
                    .setCount(cartProduct.getCount());
        }
    }
}
