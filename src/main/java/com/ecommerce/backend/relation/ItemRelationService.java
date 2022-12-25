package com.ecommerce.backend.relation;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.response.ItemResponse;
import com.ecommerce.backend.service.ItemImageService;
import com.ecommerce.backend.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRelationService {
    private final ItemService itemService;
    private final ItemImageService itemImageService;


    public Page<ItemResponse.Read> display(Pageable pageable, Long categoryId) {
        Page<Item> itemList;

        if (categoryId == null) {
            itemList = itemService.readAll(pageable);
        } else {
            itemList = itemService.readAllByCategoryId(pageable, categoryId);
        }

        return itemList.map(item -> ItemResponse.Read.of(item, item.getItemImageList()));
    }

    public Page<ItemResponse.Read> search(Pageable pageable, String searchTitle, Long categoryId) {
        Page<Item> itemList;

        if (categoryId == null) {
            itemList = itemService.readAllByNameContaining(pageable, searchTitle);
        } else {
            itemList = itemService.readAllByNameContainingAndCategoryId(pageable, searchTitle, categoryId);
        }

        return itemList.map(item -> ItemResponse.Read.of(item, item.getItemImageList()));
    }
}