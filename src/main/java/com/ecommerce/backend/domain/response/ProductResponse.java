package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Product;
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
    public static class ReadResponse {
        private String productName;

        private Integer price;

        private Integer quantity;   // 제품의 총 개수

        private Integer soldCount;  // 제품의 판매 개수

        private String description;

        @Enumerated(EnumType.STRING)
        private ProductStatus productStatus;

        private List<ProductImageResponse.ReadResponse> ProductImageList = new ArrayList<>();

        public static ReadResponse fromProduct(Product entity) {
            return new ReadResponse()
                    .setProductName(entity.getProductName())
                    .setPrice(entity.getPrice())
                    .setQuantity(entity.getQuantity())
                    .setSoldCount(entity.getSoldCount())
                    .setDescription(entity.getDescription())
                    .setProductStatus(entity.getProductStatus())
                    .setProductImageList(entity.getProductImageList().stream()
                            .map(productImage -> ProductImageResponse.ReadResponse.fromProduct(productImage))
                            .collect(Collectors.toList()));
        }
    }
}