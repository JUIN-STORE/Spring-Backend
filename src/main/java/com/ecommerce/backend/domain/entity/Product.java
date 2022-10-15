package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.ProductStatus;
import com.ecommerce.backend.exception.NotEnoughStockException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
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

    private Integer soldCount;  // 제품의 판매 개수, quantity가 업데이트될 수 있어서 필요

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    // product 테이블에 category_id 컬럼을 만들어 준다.
    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    // 테이블에 아무 것도 안 생김.
    // 읽기 전용, 연관관계 주인 아님
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductImage> productImageList = new ArrayList<>();

    // 테이블에 아무 것도 안 생김.
    // 읽기 전용, 연관관계 주인 아님
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderProduct> orderProductList = new ArrayList<>();

    // 읽기용 매핑
    public void addProductImageList(ProductImage productImage){
        this.productImageList.add(productImage);
        if (productImage.getProduct() != this){
            productImage.initProduct(this);
        }
    }

    // 재고 증가
    public void addQuantity(Integer quantity){
        this.quantity += quantity;
        this.soldCount -= quantity;
    }
    
    // 재고 삭제
    public void removeQuantity(Integer quantity){
        int restQuantity = this.quantity - quantity;

        if(restQuantity < 0){
            throw new NotEnoughStockException("Need More Stock. Current Stock: " + restQuantity);
        }
        this.quantity = restQuantity;
        this.soldCount += quantity;
    }

    public Integer getTotalPrice(){
        return this.price * this.soldCount;
    }

    public static List<Long> makeProductIdList(Page<Product> productList) {
        return productList.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
    }
}