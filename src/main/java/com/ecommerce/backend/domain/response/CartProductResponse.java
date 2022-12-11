package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.CartProduct;
import lombok.Data;
import lombok.experimental.Accessors;

public class CartProductResponse {
    @Data
    @Accessors(chain = true)
    public static class Read {
        // cart
        private Long productId;
        private Integer count;                  // 카트 안에 들어 있는 제품의 총 개수

        // product
        private String productName;             // 제품의 이름
        private Integer price;                  // 제품의 가격
        private String description;             // 제품 설명

        // productImageResponse
        private String imageName;               // 이미지 파일명
        private String originImageName;         // 원본 이미지 파일명
        private String imageUrl;                // 이미지 조회 경로
        private Boolean thumbnail;              // 썸네일 여부
    }

    @Data
    @Accessors(chain = true)
    public static class Create {
        private Long productId;

        private int count;

        public static Create of(CartProduct cartProduct) {
            return new Create()
                    .setProductId(cartProduct.getProduct().getId())
                    .setCount(cartProduct.getCount());
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Delete {
        private Long productId;

        private int count;

        public static Create from(CartProduct cartProduct) {
            return new Create()
                    .setProductId(cartProduct.getProduct().getId())
                    .setCount(cartProduct.getCount());
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Buy {
        private Integer count; // 제품의 총 개수

        private ProductResponse.Buy product;

        private ProductImageResponse.Buy productImage;

        public static CartProductResponse.Buy from(CartProductResponse.Read read) {
            var productResponse = new ProductResponse.Buy()
                    .setProductId(read.getProductId())
                    .setProductName(read.getProductName())
                    .setPrice(read.getPrice())
                    .setDescription(read.getDescription());

            var productImageResponse = new ProductImageResponse.Buy()
                    .setImageUrl(read.getImageUrl())
                    .setImageName(read.getImageName())
                    .setOriginImageName(read.getOriginImageName())
                    .setThumbnail(read.getThumbnail());

            return new CartProductResponse.Buy()
                    .setProduct(productResponse)
                    .setProductImage(productImageResponse);
        }
    }
}
