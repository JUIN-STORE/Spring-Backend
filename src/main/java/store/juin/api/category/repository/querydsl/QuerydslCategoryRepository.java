package store.juin.api.category.repository.querydsl;

import org.springframework.stereotype.Repository;
import store.juin.api.category.model.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslCategoryRepository {
    Optional<List<Category>> findAllByParentIsNull();
}
