package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.exception.NotEnoughStockException;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, length = 50)
    private String productName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer quantity; // 제품의 총 개수

    private Integer soldCount;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnailPath;

    private String originImagePath;

    @CreationTimestamp
    private LocalDateTime createdAt; // 등록 시간

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 수정 시간

//    @NonNull
//    @ManyToOne
//    @JoinColumn(name = "seller_id")
//    private Account seller;

//    @ManyToMany
//    @JoinTable(
//            name = "product_category",
//            joinColumns = @JoinColumn(name = "product_id"),
//            inverseJoinColumns = @JoinColumn(name = "category_id")
//    )
//    private Set<Category> categories;

//    @ManyToMany
//    @JoinTable(
//            name = "product_tag",
//            joinColumns = @JoinColumn(name = "product_id"),
//            inverseJoinColumns = @JoinColumn(name = "tag_id")
//    )

    // 재고 증가
    public void addQuantity(Integer quantity){
        this.quantity += quantity;
    }
    
    // 재고 삭제
    public void removeQuantity(Integer quantity){
        int restQuantity = this.quantity = quantity;
        if(restQuantity < 0 ){
            throw new NotEnoughStockException("Need More Stock");
        }
        this.quantity = restQuantity;
    }
}