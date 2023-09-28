package store.juin.api.itemcategory.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.item.model.entity.Item;
import store.juin.api.itemimage.model.entity.ItemImage;

@Data
@Accessors(chain = true)
public class ItemImageCreateRequest {

    private String imageName;           // 원본 이미지 파일명을 통해 새롭게 저장될 이미지 파일명

    private String originImageName;     // 원본 이미지 파일명

    private String imageUrl;            // 이미지 조회 경로

    private boolean thumbnail;          // 썸네일 여부 (리사이징 처리 여부)

    private boolean representative;     // 대표 이미지 여부

    public ItemImageCreateRequest(String originImageName, boolean representative) {
        this.originImageName = originImageName;
        this.representative = representative;
    }

    public ItemImage toItemImage(Item item, String imageName, String imageUrl, boolean thumbnail) {
        return ItemImage.builder()
                .item(item)
                .name(imageName)
                .originName(this.originImageName)
                .representative(this.representative)
                .imageUrl(imageUrl)
                .thumbnail(thumbnail)
                .build();
    }
}