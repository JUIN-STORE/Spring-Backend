package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import lombok.Data;
import lombok.experimental.Accessors;

public class ItemImageRequest {
    @Data
    @Accessors(chain = true)
    public static class Create {
        private String imageName;           // 원본 이미지 파일명을 통해 새롭게 저장될 이미지 파일명

        private String originImageName;     // 원본 이미지 파일명

        private String imageUrl;            // 이미지 조회 경로

        private boolean thumbnail;          // 썸네일 여부

        public Create(String originImageName) {
            this.originImageName = originImageName;
        }

        public ItemImage toItemImage(Item item, String imageName, String imageUrl, boolean thumbnail) {
            return ItemImage.builder()
                    .item(item)
                    .name(imageName)
                    .originName(this.originImageName)
                    .imageUrl(imageUrl)
                    .thumbnail(thumbnail)
                    .build();
        }
    }
}