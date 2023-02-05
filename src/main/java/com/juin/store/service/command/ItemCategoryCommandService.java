package com.juin.store.service.command;

import com.juin.store.domain.entity.Category;
import com.juin.store.domain.entity.Item;
import com.juin.store.domain.entity.ItemCategory;
import com.juin.store.repository.jpa.ItemCategoryRepository;
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
