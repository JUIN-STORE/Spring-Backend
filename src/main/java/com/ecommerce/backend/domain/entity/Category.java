package com.ecommerce.backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;

    private Long depth;

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent")
    private List<Category> childList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<ProductCategory> productCategoryList = new ArrayList<>();
}