package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.repository.querydsl.QuerydslCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, QuerydslCategoryRepository {
    Optional<Category> findById(Long categoryId);
}
