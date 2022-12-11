package com.ecommerce.backend.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class ItemCategory {
    @Id @Column(name = "item_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // item_category 테이블에 item_id 컬럼 생김
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    // item_category 테이블에 category_id 컬럼 생김
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public ItemCategory(Item item, Category category) {
        this.item = item;
        this.category = category;
    }
}