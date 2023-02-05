package com.juin.store.repository.jpa;

import com.juin.store.domain.entity.Item;
import com.juin.store.repository.querydsl.QuerydslItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslItemRepository {
    Page<Item> findAll(Pageable pageable);

    Page<Item> findAllByCategoryId(Pageable pageable, Long category);

    // 참고 https://yonguri.tistory.com/122
    Page<Item> findAllByNameContaining(Pageable pageable, String name);

    Page<Item> findAllByNameContainingAndCategoryId(Pageable pageable, String name, Long categoryId);

    Long countByNameContaining(String name);

    Page<Item> findAllByPersonalColor(Pageable pageable, String personalColor);
}
