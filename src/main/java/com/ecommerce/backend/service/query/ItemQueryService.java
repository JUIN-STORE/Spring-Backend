package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.response.ItemResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemQueryService {
    private final ItemRepository itemRepository;

    private final ItemImageQueryService itemImageQueryService;

    @Transactional(readOnly = true)
    public Item readById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Item> readAllByIdList(List<Long> itemIdList) {
        return itemRepository.findAllByIdIn(itemIdList)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<Item> readAll(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Item> readAllByCategoryId(Pageable pageable, Long categoryId) {
        return itemRepository.findAllByCategoryId(pageable, categoryId);
    }

    @Transactional(readOnly = true)
    public Page<Item> readAllByNameContaining(Pageable pageable, String searchTitle) {
        return itemRepository.findAllByNameContaining(pageable, searchTitle);
    }

    @Transactional(readOnly = true)
    public Page<Item>
    readAllByNameContainingAndCategoryId(Pageable pageable, String searchTitle, Long categoryId) {
        return itemRepository.findAllByNameContainingAndCategoryId(pageable, searchTitle, categoryId);
    }

    @Transactional(readOnly = true)
    public long total() {
        return itemRepository.count();
    }

    @Transactional(readOnly = true)
    public Long totalByNameContaining(String searchTitle) {
        return itemRepository.countByNameContaining(searchTitle);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse.Read> display(Pageable pageable, Long categoryId) {
        Page<Item> itemList;

        if (categoryId == null) {
            itemList = readAll(pageable);
        } else {
            itemList = readAllByCategoryId(pageable, categoryId);
        }

        return makeItemReadResponseList(itemList);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse.Read> search(Pageable pageable, String searchTitle, Long categoryId) {
        Page<Item> itemList;

        if (categoryId == null) {
            itemList = readAllByNameContaining(pageable, searchTitle);
        } else {
            itemList = readAllByNameContainingAndCategoryId(pageable, searchTitle, categoryId);
        }

        return makeItemReadResponseList(itemList);
    }

    private List<ItemResponse.Read> makeItemReadResponseList(Page<Item> itemList) {
        final List<Long> itemIdList = itemList.stream().map(Item::getId).collect(Collectors.toList());
        final List<ItemImage> itemImageList = itemImageQueryService.readAllByItemIdIn(itemIdList);
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