package store.juin.api.service.query;

import store.juin.api.domain.entity.Item;
import store.juin.api.domain.response.ItemResponse;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemQueryService {
    private final ItemRepository itemRepository;

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
    public Page<Item> readAllByPersonalColor(Pageable pageable, String personalColor) {
        return itemRepository.findAllByPersonalColor(pageable, personalColor);
    }

    @Transactional(readOnly = true)
    public Page<ItemResponse.Read> search(Pageable pageable,
                                          String searchTitle,
                                          Long categoryId,
                                          String personalColor) {
        Page<Item> itemList;

        if (personalColor != null) {
            itemList = readAllByPersonalColor(pageable, personalColor);
        } else {
            itemList = itemRepository
                    .findByNameContainingAndCategoryId(pageable, searchTitle, categoryId)
                    .orElse(new PageImpl<>(Collections.emptyList()));
        }

        return itemList.map(item -> ItemResponse.Read.of(item, item.getItemImageList()));
    }

    @Transactional(readOnly = true)
    public Page<ItemResponse.Read> display(Pageable pageable) {
        final Page<Item> itemList = readAll(pageable);

        return itemList.map(item -> ItemResponse.Read.of(item, item.getItemImageList()));
    }
}