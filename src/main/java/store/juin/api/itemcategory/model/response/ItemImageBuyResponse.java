package store.juin.api.itemcategory.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ItemImageBuyResponse {
    private String name;           // 상품명

    private String imageName;     // 이미지 파일명

    private String imageUrl;            // 이미지 조회 경로

    private Boolean thumbnail;          // 썸네일 여부

    private Boolean representative;
}
