package store.juin.api.category.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.juin.api.category.model.entity.Category;
import store.juin.api.category.repository.querydsl.QuerydslCategoryRepository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, QuerydslCategoryRepository {
    Optional<Category> findById(Long categoryId);
}
