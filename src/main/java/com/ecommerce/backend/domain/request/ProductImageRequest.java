package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import lombok.Data;
import lombok.experimental.Accessors;

public class ProductImageRequest {
    @Data
    @Accessors(chain = true)
    public static class Create {
        private String imageName;           // 이미지 파일명

        private String originImageName;     // 원본 이미지 파일명

        private String imageUrl;            // 이미지 조회 경로

        private Boolean thumbnail;          // 썸네일 여부

        public Create(Boolean isThumbnail) {
            this.thumbnail = isThumbnail;
        }

        public ProductImage toProductImage(Product product, String imageName, String imageUrl, String originImageName) {
            return ProductImage.builder()
                    .product(product)
                    .imageName(imageName)
                    .originImageName(originImageName)
                    .imageUrl(imageUrl)
                    .thumbnail(this.thumbnail)
                    .build();
        }
    }
}