package com.ecommerce.backend.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class ProductCategory {
    @Id @Column(name = "product_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // product_category 테이블에 product_id 컬럼 생김
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // product_category 테이블에 category_id 컬럼 생김
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public ProductCategory(Product product, Category category) {
        this.product = product;
        this.category = category;
    }
}