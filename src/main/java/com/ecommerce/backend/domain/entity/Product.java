package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.ProductStatus;
import com.ecommerce.backend.exception.NotEnoughStockException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {
    @Id @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String productName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer quantity;   // 제품의 총 개수

    private Integer soldCount;  // 제품의 판매 개수

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

//    @NonNull
//    @ManyToOne
//    @JoinColumn(name = "seller_id")
//    private Account seller;

    // 재고 증가
    public void addQuantity(Integer quantity){
        this.quantity += quantity;
    }
    
    // 재고 삭제
    public void removeQuantity(Integer quantity){
        int restQuantity = this.quantity - quantity;
        if(restQuantity < 0){
            throw new NotEnoughStockException("Need More Stock. Current Stock: " + restQuantity);
        }
        this.quantity = restQuantity;
    }
}