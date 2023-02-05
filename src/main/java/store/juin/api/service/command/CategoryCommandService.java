package store.juin.api.service.command;

import store.juin.api.domain.entity.Category;
import store.juin.api.domain.request.CategoryRequest;
import store.juin.api.repository.jpa.CategoryRepository;
import store.juin.api.service.query.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCommandService {
    private final CategoryRepository categoryRepository;

    private final CategoryQueryService categoryQueryService;

    @Transactional
    public Long add(CategoryRequest.Create request) {
        final Long parentId = request.getParentId();

        Category parent = null;

        if (parentId > 0) {
            // 0보다 크면 하위 카테고리, 아니면 최상위 카테고리
            parent = categoryQueryService.readById(parentId);
        }

        final Category category = request.toCategory(parent);
        final Category save = categoryRepository.save(category);

        return save.getId();
    }
}
