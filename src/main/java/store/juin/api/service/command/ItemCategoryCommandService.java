package store.juin.api.service.command;

import store.juin.api.domain.entity.Category;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.entity.ItemCategory;
import store.juin.api.repository.jpa.ItemCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemCategoryCommandService {
    private final ItemCategoryRepository itemCategoryRepository;

    public void add(Item item, Category category) {
        final ItemCategory itemCategory = new ItemCategory(item, category);
        itemCategoryRepository.save(itemCategory);
    }
}
