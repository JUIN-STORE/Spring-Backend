package com.ecommerce.backend.service.command;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemCategory;
import com.ecommerce.backend.repository.jpa.ItemCategoryRepository;
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
