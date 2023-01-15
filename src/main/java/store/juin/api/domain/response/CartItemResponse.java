package store.juin.api.domain.response;

import store.juin.api.domain.entity.CartItem;
import lombok.Data;
import lombok.experimental.Accessors;

public class CartItemResponse {
    @Data
    @Accessors(chain = true)
    public static class Retrieve {
        // cart
        private Long itemId;
        private Integer count;                  // 카트 안에 들어 있는 제품의 총 개수

        // item
        private String itemName;                // 제품의 이름
        private Integer price;                  // 제품의 가격
        private String description;             // 제품 설명

        // itemImageResponse
        private String itemImageName;           // 이미지 파일명
        private String originImageName;         // 원본 이미지 파일명
        private String imageUrl;                // 이미지 조회 경로
        private Boolean thumbnail;              // 썸네일 여부
    }

    @Data
    @Accessors(chain = true)
    public static class Create {
        private Long itemId;

        private int count;

        public static Create of(CartItem cartItem) {
            return new Create()
                    .setItemId(cartItem.getItem().getId())
                    .setCount(cartItem.getCount());
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Delete {
        private Long itemId;

        private int count;

        public static Create from(CartItem cartItem) {
            return new Create()
                    .setItemId(cartItem.getItem().getId())
                    .setCount(cartItem.getCount());
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Buy {
        private Integer count; // 제품의 총 개수

        private ItemResponse.Buy item;

        private ItemImageResponse.Buy itemImage;

        public static CartItemResponse.Buy from(Retrieve retrieve) {
            var itemResponse = new ItemResponse.Buy()
                    .setItemId(retrieve.getItemId())
                    .setItemName(retrieve.getItemName())
                    .setPrice(retrieve.getPrice())
                    .setDescription(retrieve.getDescription());

            var itemImageResponse = new ItemImageResponse.Buy()
                    .setImageUrl(retrieve.getImageUrl())
                    .setName(retrieve.getItemName())
                    .setOriginName(retrieve.getOriginImageName())
                    .setThumbnail(retrieve.getThumbnail());

            return new CartItemResponse.Buy()
                    .setCount(retrieve.getCount())
                    .setItem(itemResponse)
                    .setItemImage(itemImageResponse);
        }
    }
}