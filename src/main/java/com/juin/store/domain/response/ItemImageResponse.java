package com.juin.store.domain.response;

import com.juin.store.domain.entity.ItemImage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

public class ItemImageResponse {
    @Data @Accessors(chain = true)
    public static class Retrieve {

        private String imageName;           // 이미지 파일명

        private String originName;     // 원본 이미지 파일명

        private String imageUrl;            // 이미지 조회 경로

        private Boolean thumbnail;          // 썸네일 여부

        public static Retrieve of(ItemImage itemImage) {
            return new Retrieve()
                    .setImageName(itemImage.getName())
                    .setOriginName(itemImage.getOriginName())
                    .setImageUrl(itemImage.getImageUrl())
                    .setThumbnail(itemImage.getThumbnail());
        }

        public static List<Retrieve> of(List<ItemImage> itemImage) {
            List<Retrieve> response = new ArrayList<>();

            for (ItemImage image : itemImage) {
                response.add(Retrieve.of(image));
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