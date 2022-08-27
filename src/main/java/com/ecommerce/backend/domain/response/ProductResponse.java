package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.enums.ProductStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductResponse {
    @Data @Accessors(chain = true)
    public static class Read {
        private Long id;

        private String productName;             // 제품의 이름

        private Integer price;                  // 제품의 가격

        private Integer quantity;               // 제품의 총 개수

        private Integer soldCount;              // 제품의 판매 개수

        private String description;             // 제품 설명

        @Enumerated(EnumType.STRING)
        private ProductStatus productStatus;    // 제품의 상태

        private List<ProductImageResponse.Read> ProductImageList = new ArrayList<>();
        
        // 단건
        public static Read from(Product product) {
            return new Read()
                    .setId(product.getId())
                    .setProductName(product.getProductName())
                    .setPrice(product.getPrice())
                    .setQuantity(product.getQuantity())
                    .setSoldCount(product.getSoldCount())
                    .setDescription(product.getDescription())
                    .setProductStatus(product.getProductStatus())
                    .setProductImageList(product.getProductImageList().stream() // 이 시점에 쿼리 나감
                            .map(ProductImageResponse.Read::of)
                            .collect(Collectors.toList()));
        }

        // 다건
        public static Read of(Product product, ProductImage productImage) {
            return new Read()
                    .setId(product.getId())
                    .setProductName(product.getProductName())
                    .setPrice(product.getPrice())
                    .setQuantity(product.getQuantity())
                    .setSoldCount(product.getSoldCount())
                    .setDescription(product.getDescription())
                    .setProductStatus(product.getProductStatus())
                    .setProductImageList(List.of(ProductImageResponse.Read.of(productImage)));
        }
    }
}