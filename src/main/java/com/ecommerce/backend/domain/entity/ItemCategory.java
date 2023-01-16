package com.ecommerce.backend.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCategory extends BaseEntity {
    @Id @Column(name = "item_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // item_category 테이블에 item_id 컬럼 생김
    @NotNull
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    // item_category 테이블에 category_id 컬럼 생김
    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public ItemCategory(Item item, Category category) {
        this.item = item;
        this.category = category;
    }
}