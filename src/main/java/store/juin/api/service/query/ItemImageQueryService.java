package store.juin.api.service.query;

import store.juin.api.domain.entity.ItemImage;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemImageQueryService {
    private final ItemImageRepository itemImageRepository;

    @Transactional(readOnly = true)
    public List<ItemImage> readAllByThumbnail(boolean thumbnail) {
        return itemImageRepository.findAllByThumbnail(thumbnail)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_THUMBNAIL_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ItemImage> readAllByItemIdIn(List<Long> itemIdList) {
        return itemImageRepository.findAllByItemIdIn(itemIdList)
                .orElse(new ArrayList<>());
    }
}