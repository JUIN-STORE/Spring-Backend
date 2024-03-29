package store.juin.api.itemimage.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import store.juin.api.common.model.entity.BaseEntity;
import store.juin.api.item.model.entity.Item;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImage extends BaseEntity {
    @Id @Column(name = "item_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;                // 원본 이미지 파일명을 통해 새로 만든 이미지 파일명

    @NotNull
    private String originName;          // 원본 이미지 파일명

    @NotNull
    private String imageUrl;            // 이미지 조회 경로

    private Boolean thumbnail;          // 썸네일 여부

    private Boolean representative;

    // 연관관계 주인
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void updateItemImage(String originName, String name, String imageUrl){
        this.originName = originName;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void initItem(Item item) {
        // 기존 Item와의 연관관계 제거
        if (this.item != null) this.item.getItemImageList().remove(this);

        this.item = item;

        // 무한루프 빠지지 않도록 처리
        if (!item.getItemImageList().contains(this)) item.getItemImageList().add(this);
    }
}
