package store.juin.api.itemcategory.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.itemimage.model.entity.ItemImage;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ItemImageResponse {
    @Data @Accessors(chain = true)
    public static class Retrieve {

        private String imageName;           // 이미지 파일명

        private String originName;     // 원본 이미지 파일명

        private String imageUrl;            // 이미지 조회 경로

        private Boolean thumbnail;          // 썸네일 여부

        private Boolean representative;

        public static Retrieve of(ItemImage itemImage) {
            return new Retrieve()
                    .setImageName(itemImage.getName())
                    .setOriginName(itemImage.getOriginName())
                    .setImageUrl(itemImage.getImageUrl())
                    .setThumbnail(itemImage.getThumbnail())
                    .setRepresentative(itemImage.getRepresentative());
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
        private String name;           // 상품명

        private String imageName;     // 이미지 파일명

        private String imageUrl;            // 이미지 조회 경로

        private Boolean thumbnail;          // 썸네일 여부

        private Boolean representative;
    }
}