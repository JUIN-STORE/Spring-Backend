package store.juin.api.itemcategory.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.itemimage.model.entity.ItemImage;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ItemImageRetrieveResponse {
    private String imageName;           // 이미지 파일명

    private String originName;          // 원본 이미지 파일명

    private String imageUrl;            // 이미지 조회 경로

    private Boolean thumbnail;          // 썸네일 여부

    private Boolean representative;

    public static ItemImageRetrieveResponse of(ItemImage itemImage) {
        return new ItemImageRetrieveResponse()
                .setImageName(itemImage.getName())
                .setOriginName(itemImage.getOriginName())
                .setImageUrl(itemImage.getImageUrl())
                .setThumbnail(itemImage.getThumbnail())
                .setRepresentative(itemImage.getRepresentative());
    }

    public static List<ItemImageRetrieveResponse> of(List<ItemImage> itemImage) {
        List<ItemImageRetrieveResponse> response = new ArrayList<>();

        for (ItemImage image : itemImage) {
            response.add(ItemImageRetrieveResponse.of(image));
        }

        return response;
    }
}