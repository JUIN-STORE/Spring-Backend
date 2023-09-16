package store.juin.api.itemimage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import store.juin.api.common.handler.CommandTransactional;
import store.juin.api.item.model.entity.Item;
import store.juin.api.itemcategory.model.request.ItemImageRequest;
import store.juin.api.itemimage.model.entity.ItemImage;
import store.juin.api.itemimage.repository.jpa.ItemImageRepository;
import store.juin.api.upload.service.FileUploadService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemImageCommandService {
    private final CommandTransactional commandTransactional;

    private final ItemImageRepository itemImageRepository;

    private final FileUploadService fileUploadService;

    public void add(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        commandTransactional.execute(() -> {
            addOriginalImage(request, multipartFile, item);
            addThumbnailImage(request, multipartFile, item);
        });
    }

    // 원본 파일 저장
    private void addOriginalImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        final ItemImage itemImage = fileUploadService.addOriginalImage(request, multipartFile, item);
        itemImageRepository.save(itemImage);
    }

    // 썸네일 생성 및 저장
    private void addThumbnailImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        final String extension = StringUtils.getFilenameExtension(request.getOriginImageName());
        final ItemImage itemImage = fileUploadService.addThumbnailImage(request, multipartFile, item, extension);
        itemImageRepository.save(itemImage);
    }
}
