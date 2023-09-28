package store.juin.api.category.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.cart.model.request.CategoryCreateRequest;
import store.juin.api.category.model.entity.Category;
import store.juin.api.category.repository.jpa.CategoryRepository;
import store.juin.api.category.service.query.CategoryQueryService;
import store.juin.api.common.handler.CommandTransactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCommandService {
    private final CommandTransactional commandTransactional;

    private final CategoryRepository categoryRepository;

    private final CategoryQueryService categoryQueryService;

    public Long add(CategoryCreateRequest request) {
        final Long parentId = request.getParentId();

        Category parent = null;

        if (parentId > 0) {
            // 0보다 크면 하위 카테고리, 아니면 최상위 카테고리
            parent = categoryQueryService.readById(parentId);
        }

        final Category category = request.toCategory(parent);

        return commandTransactional.execute(() -> {
            Category save = categoryRepository.save(category);

            return save.getId();
        });
    }
}
