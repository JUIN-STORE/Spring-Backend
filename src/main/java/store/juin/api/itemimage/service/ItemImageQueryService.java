package store.juin.api.itemimage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.QueryTransactional;
import store.juin.api.itemimage.model.entity.ItemImage;
import store.juin.api.itemimage.repository.jpa.ItemImageRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemImageQueryService {
    private final QueryTransactional queryTransactional;

    private final ItemImageRepository itemImageRepository;

    public List<ItemImage> readAllByThumbnail(boolean thumbnail) {
        return queryTransactional.execute(() ->
                itemImageRepository.findAllByThumbnail(thumbnail)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_THUMBNAIL_NOT_FOUND))
        );
    }

    public List<ItemImage> readAllByItemIdIn(List<Long> itemIdList) {
        return queryTransactional.execute(() ->
                itemImageRepository.findAllByItemIdIn(itemIdList).orElse(new ArrayList<>())
        );
    }
}