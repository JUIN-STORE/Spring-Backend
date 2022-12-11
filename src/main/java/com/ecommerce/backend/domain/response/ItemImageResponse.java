package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.ItemImage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

public class ItemImageResponse {
    @Data @Accessors(chain = true)
    public static class Read {

        private String imageName;           // 이미지 파일명

        private String originName;     // 원본 이미지 파일명

        private String imageUrl;            // 이미지 조회 경로

        private Boolean thumbnail;          // 썸네일 여부

        public static Read of(ItemImage itemImage) {
            return new Read()
                    .setImageName(itemImage.getName())
                    .setOriginName(itemImage.getOriginName())
                    .setImageUrl(itemImage.getImageUrl())
                    .setThumbnail(itemImage.getThumbnail());
        }

        public static List<Read> of(List<ItemImage> itemImage) {
            List<ItemImageResponse.Read> response = new ArrayList<>();

            for (ItemImage image : itemImage) {
                response.add(ItemImageResponse.Read.of(image));
            }

            return response;
        }
    }

    @Data @Accessors(chain = true)
    public static class Buy {
        private String name;           // 이미지 파일명

        private String originName;     // 원본 이미지 파일명

        private String imageUrl;            // 이미지 조회 경로

        private Boolean thumbnail;          // 썸네일 여부
    }
}