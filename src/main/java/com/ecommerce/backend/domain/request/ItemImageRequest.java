package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import lombok.Data;
import lombok.experimental.Accessors;

public class ItemImageRequest {
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

        public ItemImage toItemImage(Item item, String imageName, String imageUrl, String originImageName) {
            return ItemImage.builder()
                    .item(item)
                    .imageName(imageName)
                    .originImageName(originImageName)
                    .imageUrl(imageUrl)
                    .thumbnail(this.thumbnail)
                    .build();
        }
    }
}