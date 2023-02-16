package store.juin.api.service.command;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import store.juin.api.domain.entity.Category;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.enums.ItemStatus;
import store.juin.api.domain.request.ItemImageRequest;
import store.juin.api.domain.request.ItemRequest;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.ItemRepository;
import store.juin.api.service.query.CategoryQueryService;
import store.juin.api.service.query.ItemQueryService;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemCommandService {
    private final ItemRepository itemRepository;

    private final ItemQueryService itemQueryService;
    private final CategoryQueryService categoryQueryService;

    private final ItemImageCommandService itemImageCommandService;
    private final ItemCategoryCommandService itemCategoryCommandService;

    @Transactional
    public Long add(ItemRequest.Create request
            , MultipartFile representativeFile,
                    List<MultipartFile> itemImageFileList) throws IOException {
        if (representativeFile == null) throw new InvalidParameterException(Msg.ITEM_THUMBNAIL_REQUIRED);

        // 상품 등록
        final Category category = categoryQueryService.readById(request.getCategoryId());
        final Item item = request.toItem(category);

        itemRepository.save(item);
        itemCategoryCommandService.add(item, category);

        // 대표 이미지
        final String originFileName = representativeFile.getOriginalFilename();
        validOriginalFilename(originFileName);
        itemImageCommandService.add(new ItemImageRequest.Create(originFileName, true), representativeFile, item);

        final Long itemId = item.getId();

        // 썸네일 외 이미지 없으면 리턴
        if (Collections.isEmpty(itemImageFileList)) return itemId;

        // 썸네일 외 상세 이미지 등록
        for (MultipartFile itemImageFile : itemImageFileList) {
            if (itemImageFile == null) {
                log.warn("[P1][SERV][ICM][IIF_]: 요청에 null인 이미지가 포함되어 있습니다. request=({})", request);
                continue;
            }

            final String originalFilename = itemImageFile.getOriginalFilename();
            validOriginalFilename(originalFilename);
            itemImageCommandService.add(new ItemImageRequest.Create(originalFilename, false), itemImageFile, item);
        }

        return itemId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long remove(Long itemId) {
        final Item item = itemQueryService.readById(itemId);
        item.updateStatus(ItemStatus.SOLD_OUT);

        return item.getId();
    }

    public void validOriginalFilename(String originalFilename) {
        if (!StringUtils.hasText((originalFilename))) {
            throw new InvalidFileNameException(originalFilename, Msg.ILLEGAL_ITEM_IMAGE_FILE_NAME);
        }
    }
}