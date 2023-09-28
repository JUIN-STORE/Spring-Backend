package store.juin.api.item.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.juin.api.account.enumeration.PersonalColor;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.QueryTransactional;
import store.juin.api.item.model.entity.Item;
import store.juin.api.item.model.response.ItemRetrieveResponse;
import store.juin.api.item.repository.jpa.ItemRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemQueryService {
    private final QueryTransactional queryTransactional;

    private final ItemRepository itemRepository;

    public Item readById(Long itemId) {
        return queryTransactional.execute(() ->
                itemRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_NOT_FOUND))
        );
    }

    public List<Item> readAllByIdList(List<Long> itemIdList) {
        return queryTransactional.execute(() ->
                itemRepository.findAllByIdIn(itemIdList)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_NOT_FOUND))
        );
    }

    public Page<Item> readAll(Pageable pageable) {
        return queryTransactional.execute(() ->
                itemRepository.findAll(pageable)
        );
    }

    public Page<Item> readAllByCategoryId(Pageable pageable, Long categoryId) {
        return queryTransactional.execute(() ->
                itemRepository.findAllByCategoryId(pageable, categoryId)
        );
    }

    public Page<Item> readAllByPersonalColor(Pageable pageable, PersonalColor personalColor) {
        return queryTransactional.execute(() ->
                itemRepository.findAllByPersonalColor(pageable, personalColor)
        );
    }

    public Page<ItemRetrieveResponse> search(Pageable pageable, String searchTitle, Long categoryId, PersonalColor personalColor) {
        return queryTransactional.execute(() -> {
            Page<Item> itemList;

            if (personalColor != null) {
                itemList = readAllByPersonalColor(pageable, personalColor);
            } else {
                itemList = itemRepository
                        .findByNameContainingAndCategoryId(pageable, searchTitle, categoryId)
                        .orElse(new PageImpl<>(Collections.emptyList()));
            }

            return itemList.map(item -> ItemRetrieveResponse.of(item, item.getItemImageList()));
        });
    }

    public Page<ItemRetrieveResponse> display(Pageable pageable) {
        final Page<Item> itemList = readAll(pageable);

        return itemList.map(item -> ItemRetrieveResponse.of(item, item.getItemImageList()));
    }
}