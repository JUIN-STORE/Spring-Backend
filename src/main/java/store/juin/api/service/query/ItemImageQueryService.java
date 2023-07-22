package store.juin.api.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.ItemImage;
import store.juin.api.exception.Msg;
import store.juin.api.handler.QueryTransactional;
import store.juin.api.repository.jpa.ItemImageRepository;

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