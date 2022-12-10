package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.ProductImage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

public class ProductImageResponse {
    @Data @Accessors(chain = true)
    public static class Read {

        private String imageName;           // 이미지 파일명

        private String originImageName;     // 원본 이미지 파일명

        private String imageUrl;            // 이미지 조회 경로

        private Boolean thumbnail;          // 썸네일 여부

        public static Read of(ProductImage productImage) {
            return new Read()
                    .setImageName(productImage.getImageName())
                    .setOriginImageName(productImage.getOriginImageName())
                    .setImageUrl(productImage.getImageUrl())
                    .setThumbnail(productImage.getThumbnail());
        }

        public static List<Read> of(List<ProductImage> productImage) {
            List<ProductImageResponse.Read> response = new ArrayList<>();

            for (ProductImage image : productImage) {
                response.add(ProductImageResponse.Read.of(image));
            }

            return response;
        }
    }

    @Data @Accessors(chain = true)
    public static class Buy {
        private String imageName;           // 이미지 파일명

        private String originImageName;     // 원본 이미지 파일명

        private String imageUrl;            // 이미지 조회 경로

        private Boolean thumbnail;          // 썸네일 여부
    }
}