package com.juin.store.repository.jpa;

import com.juin.store.domain.entity.Category;
import com.juin.store.repository.querydsl.QuerydslCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, QuerydslCategoryRepository {
    Optional<Category> findById(Long categoryId);
}
