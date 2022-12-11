package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.enums.ItemStatus;
import com.ecommerce.backend.domain.request.ItemImageRequest;
import com.ecommerce.backend.domain.request.ItemRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ItemRepository;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    private final ItemImageService itemImageService;
    private final CategoryService categoryService;
    private final ItemCategoryService itemCategoryService;

    @Transactional
    public Long add(ItemRequest.Create request,
                    MultipartFile thumbnailImage,
                    List<MultipartFile> itemImageFileList) throws IOException {
        if (thumbnailImage == null) throw new InvalidParameterException(Msg.ITEM_THUMBNAIL_REQUIRED);

        // 상품 등록
        final Category category = categoryService.readById(request.getCategoryId());
        final Item item = request.toItem(category);

        itemCategoryService.add(item, category);
        itemRepository.save(item);

        // 썸네일 등록
        itemImageService.add(new ItemImageRequest.Create(true), thumbnailImage, item);

        final Long itemId = item.getId();

        // 썸네일 외 이미지 없으면 리턴
        if (Collections.isEmpty(itemImageFileList)) return itemId;

        // 썸네일 외 이미지 등록
        for (MultipartFile itemImageFile : itemImageFileList) {
            itemImageService.add(new ItemImageRequest.Create(false), itemImageFile, item);
        }

        return itemId;
    }


    public Item readById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_NOT_FOUND));
    }

    public List<Item> readAllByIdList(List<Long> itemIdList) {
        return itemRepository.findAllByIdIn(itemIdList)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_NOT_FOUND));
    }

    public Page<Item> readAll(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public Page<Item> readAllByCategoryId(Pageable pageable, Long categoryId) {
        return itemRepository.findAllByCategoryId(pageable, categoryId);
    }

    public Page<Item> readAllByNameContaining(Pageable pageable, String searchTitle) {
        return itemRepository.findAllByNameContaining(pageable, searchTitle);
    }

    public Page<Item>
    readAllByNameContainingAndCategoryId(Pageable pageable, String searchTitle, Long categoryId) {

        return itemRepository.findAllByNameContainingAndCategoryId(pageable, searchTitle, categoryId);
    }

    public long total() {
        return itemRepository.count();
    }

    public Long totalByNameContaining(String searchTitle) {
        return itemRepository.countByNameContaining(searchTitle);
    }


    @Transactional(rollbackFor = Exception.class)
    public Long remove(Long itemId) {
        final Item item = this.readById(itemId);
        item.updateStatus(ItemStatus.SOLD_OUT);

        return item.getId();
    }
}