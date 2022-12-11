package com.ecommerce.backend.service.relation;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.response.ItemResponse;
import com.ecommerce.backend.service.ItemImageService;
import com.ecommerce.backend.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRelationService {
    private final ItemService itemService;
    private final ItemImageService itemImageService;


    public List<ItemResponse.Read> display(Pageable pageable, Long categoryId) {
        Page<Item> itemList;

        if (categoryId == null) {
            itemList = itemService.readAll(pageable);
        } else {
            itemList = itemService.readAllByCategoryId(pageable, categoryId);
        }

        return makeItemReadResponseList(itemList);
    }

    public List<ItemResponse.Read> search(Pageable pageable, String searchTitle, Long categoryId) {
        Page<Item> itemList;

        if (categoryId == null) {
            itemList = itemService.readAllByNameContaining(pageable, searchTitle);
        } else {
            itemList = itemService.readAllByNameContainingAndCategoryId(pageable, searchTitle, categoryId);
        }

        return makeItemReadResponseList(itemList);
    }

    private List<ItemResponse.Read> makeItemReadResponseList(Page<Item> itemList) {
        final List<Long> itemIdList = itemList.stream().map(Item::getId).collect(Collectors.toList());
        final List<ItemImage> itemImageList = itemImageService.readAllByItemId(itemIdList);
        final Map<Long, List<ItemImage>> itemIdImageMap = new HashMap<>();

        for (ItemImage itemImage : itemImageList) {
            Item item = itemImage.getItem();
            if (item == null) continue;

            Long itemId = item.getId();
            List<ItemImage> imageListInItem = itemIdImageMap.getOrDefault(itemId, new ArrayList<>());
            imageListInItem.add(itemImage);
            itemIdImageMap.put(itemId, imageListInItem);
        }

        return itemList.stream()
                .map(image -> ItemResponse.Read.of(image, itemIdImageMap.get(image.getId())))
                .collect(Collectors.toList());
    }
}