package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.ProductImage;
import lombok.Data;
import lombok.experimental.Accessors;

public class ProductImageResponse {
    @Data @Accessors(chain = true)
    public static class ReadResponse {

        private String imageName;           // 이미지 파일명

        private String originImageName;     // 원본 이미지 파일명

        private String imageUrl;            // 이미지 조회 경로

        private Boolean thumbnail;          // 썸네일 여부

        public static ReadResponse fromProduct(ProductImage entity) {
            return new ReadResponse()
                    .setImageName(entity.getImageName())
                    .setOriginImageName(entity.getOriginImageName())
                    .setImageUrl(entity.getImageUrl())
                    .setThumbnail(entity.getThumbnail());
        }
    }
}