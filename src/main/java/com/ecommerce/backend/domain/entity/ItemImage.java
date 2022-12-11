package com.ecommerce.backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemImage extends BaseEntity {
    @Id @Column(name = "item_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageName;           // 이미지 파일명

    private String originImageName;     // 원본 이미지 파일명

    private String imageUrl;            // 이미지 조회 경로

    private Boolean thumbnail;          // 썸네일 여부

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void updateItemImage(String originImageName, String imageName, String imageUrl){
        this.originImageName = originImageName;
        this.imageName = imageName;
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
