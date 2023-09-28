package store.juin.api.item.service.command;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import store.juin.api.category.model.entity.Category;
import store.juin.api.category.service.query.CategoryQueryService;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.CommandTransactional;
import store.juin.api.item.enumeration.ItemStatus;
import store.juin.api.item.model.entity.Item;
import store.juin.api.item.model.request.ItemCreateRequest;
import store.juin.api.item.repository.jpa.ItemRepository;
import store.juin.api.item.service.query.ItemQueryService;
import store.juin.api.itemcategory.model.request.ItemImageCreateRequest;
import store.juin.api.itemcategory.service.command.ItemCategoryCommandService;
import store.juin.api.itemimage.service.ItemImageCommandService;

import java.security.InvalidParameterException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemCommandService {
    private final CommandTransactional commandTransactional;

    private final ItemRepository itemRepository;

    private final ItemQueryService itemQueryService;
    private final CategoryQueryService categoryQueryService;

    private final ItemImageCommandService itemImageCommandService;
    private final ItemCategoryCommandService itemCategoryCommandService;

    public Long add(ItemCreateRequest request, MultipartFile representativeFile, List<MultipartFile> itemImageFileList) {
        if (representativeFile == null) throw new InvalidParameterException(Msg.ITEM_THUMBNAIL_REQUIRED);

        // 상품 등록
        final Category category = categoryQueryService.readById(request.getCategoryId());
        final Item item = request.toItem(category);

        return commandTransactional.execute(() -> {
            itemRepository.save(item);
            itemCategoryCommandService.add(item, category);

            // 대표 이미지
            final String originFileName = representativeFile.getOriginalFilename();
            validOriginalFilename(originFileName);
            itemImageCommandService.add(new ItemImageCreateRequest(originFileName, true), representativeFile, item);

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
                itemImageCommandService.add(new ItemImageCreateRequest(originalFilename, false), itemImageFile, item);
            }

            return itemId;
        });
    }

    public Long remove(Long itemId) {
        return commandTransactional.execute(() -> {
            final Item item = itemQueryService.readById(itemId);
            item.updateStatus(ItemStatus.SOLD_OUT);

            return item.getId();
        });
    }

    public void validOriginalFilename(String originalFilename) {
        if (!StringUtils.hasText((originalFilename))) {
            throw new InvalidFileNameException(originalFilename, Msg.ILLEGAL_ITEM_IMAGE_FILE_NAME);
        }
    }
}