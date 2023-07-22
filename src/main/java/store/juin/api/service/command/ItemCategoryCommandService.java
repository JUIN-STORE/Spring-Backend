package store.juin.api.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.Category;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.entity.ItemCategory;
import store.juin.api.handler.CommandTransactional;
import store.juin.api.repository.jpa.ItemCategoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemCategoryCommandService {
    private final CommandTransactional commandTransactional;

    private final ItemCategoryRepository itemCategoryRepository;

    public void add(Item item, Category category) {
        final ItemCategory itemCategory = new ItemCategory(item, category);

        commandTransactional.execute(() ->
            itemCategoryRepository.save(itemCategory)
        );
    }
}
