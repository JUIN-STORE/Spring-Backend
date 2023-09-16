package store.juin.api.itemcategory.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.category.model.entity.Category;
import store.juin.api.common.handler.CommandTransactional;
import store.juin.api.item.model.entity.Item;
import store.juin.api.itemcategory.model.entity.ItemCategory;
import store.juin.api.itemcategory.repository.jpa.ItemCategoryRepository;

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
