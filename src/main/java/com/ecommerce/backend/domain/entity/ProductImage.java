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
public class ProductImage extends BaseEntity {
    @Id @Column(name = "product_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageName;           // 이미지 파일명

    private String originImageName;     // 원본 이미지 파일명

    private String imageUrl;            // 이미지 조회 경로

    private Boolean thumbnail;          // 썸네일 여부

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public void updateProductImage(String originImageName, String imageName, String imageUrl){
        this.originImageName = originImageName;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    public void initProduct(Product product) {
        // 기존 Product와의 연관관계 제거
        if (this.product != null) this.product.getProductImageList().remove(this);

        this.product = product;

        // 무한루프 빠지지 않도록 처리
        if (!product.getProductImageList().contains(this)) product.getProductImageList().add(this);
    }
}
