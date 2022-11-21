package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long categoryId);

    @Query(nativeQuery = true,
    value = "WITH RECURSIVE cte (n, category_id, category_name, depth, parent_id) AS\n" +
            "(\n" +
            "  SELECT 1, category_id, category_name, depth, parent_id\n" +
            "  FROM category AS c1\n" +
            "  UNION ALL\n" +
            "  SELECT n + 1, c2.category_id, c2.category_name, c2.depth, c2.parent_id\n" +
            "  FROM category AS c2\n" +
            "\tJOIN cte ON cte.category_id = c2.parent_id\n" +
            ")\n" +
            "SELECT category_id, category_name, depth, parent_id FROM cte")
    List<Category> findByParentId(Long parentId);
}
