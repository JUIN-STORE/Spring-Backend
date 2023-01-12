//package com.ecommerce.backend.relation;
//
//import com.ecommerce.backend.domain.entity.Item;
//import com.ecommerce.backend.domain.entity.ItemImage;
//import com.ecommerce.backend.domain.response.ItemResponse;
//import com.ecommerce.backend.service.query.ItemImageQueryService;
//import com.ecommerce.backend.service.query.ItemQueryService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ItemRelationService {
//    private final ItemQueryService itemQueryService;
//    private final ItemImageQueryService itemImageQueryService;
//
//
//    public List<ItemResponse.Read> display(Pageable pageable, Long categoryId) {
//        Page<Item> itemList;
//
//        if (categoryId == null) {
//            itemList = itemQueryService.readAll(pageable);
//        } else {
//            itemList = itemQueryService.readAllByCategoryId(pageable, categoryId);
//        }
//
//        return makeItemReadResponseList(itemList);
//    }
//
//    public List<ItemResponse.Read> search(Pageable pageable, String searchTitle, Long categoryId) {
//        Page<Item> itemList;
//
//        if (categoryId == null) {
//            itemList = itemQueryService.readAllByNameContaining(pageable, searchTitle);
//        } else {
//            itemList = itemQueryService.readAllByNameContainingAndCategoryId(pageable, searchTitle, categoryId);
//        }
//
//        return makeItemReadResponseList(itemList);
//    }
//
//    private List<ItemResponse.Read> makeItemReadResponseList(Page<Item> itemList) {
//        final List<Long> itemIdList = itemList.stream().map(Item::getId).collect(Collectors.toList());
//        final List<ItemImage> itemImageList = itemImageQueryService.readAllByItemIdIn(itemIdList);
//        final Map<Long, List<ItemImage>> itemIdImageMap = new HashMap<>();
//
//        for (ItemImage itemImage : itemImageList) {
//            Item item = itemImage.getItem();
//            if (item == null) continue;
//
//            Long itemId = item.getId();
//            List<ItemImage> imageListInItem = itemIdImageMap.getOrDefault(itemId, new ArrayList<>());
//            imageListInItem.add(itemImage);
//            itemIdImageMap.put(itemId, imageListInItem);
//        }
//
//        return itemList.stream()
//                .map(image -> ItemResponse.Read.of(image, itemIdImageMap.get(image.getId())))
//                .collect(Collectors.toList());
//    }
//}