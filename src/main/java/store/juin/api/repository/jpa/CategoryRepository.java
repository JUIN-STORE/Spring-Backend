package store.juin.api.repository.jpa;

import store.juin.api.domain.entity.Category;
import store.juin.api.repository.querydsl.QuerydslCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, QuerydslCategoryRepository {
    Optional<Category> findById(Long categoryId);
}
