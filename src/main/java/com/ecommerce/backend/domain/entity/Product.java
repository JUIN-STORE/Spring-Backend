package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.exception.NotEnoughStockException;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
//@Table (name = "PRODUCT", schema = "SHOP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;


    @NonNull
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Account seller;

    @NonNull
    private String title;

    @NonNull
    private String metaTitle;

    @NonNull
    private String slug;

    private String sku;

    private Integer price;

    /**
     * 할인되는 가격입니다 e.g. 1000원의 200원 할인 시 200원
     */
    private Integer discountPrice;

    /**
     * 제품의 총 개수 입니다
     */
    private Integer quantity;
    /**
     * 팔린 제품의 개수입니다
     */
    private Integer soldCount;

    private String thumbnailPath;

    private String imagePath;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime startsAt;

    private LocalDateTime endsAt;


    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "product_tag",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )

    //** 비즈니스 로직 **//
    // 재고 증가
//    public void addQuantity(Integer quantity){
//        this.quantity += quantity;
//    }
    public void removeQuantity(Integer quantity){
        Integer restquantity = this.quantity = quantity;
        if(restquantity < 0 ){
            throw new NotEnoughStockException("Need More Stock");
        }
        this.quantity = restquantity;
    }
}