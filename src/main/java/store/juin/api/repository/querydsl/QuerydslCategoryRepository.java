package store.juin.api.repository.querydsl;

import store.juin.api.domain.entity.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslCategoryRepository {
    Optional<List<Category>> findAllByParentIsNull();
}
