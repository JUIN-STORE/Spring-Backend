package com.ecommerce.backend.service.command;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.request.ItemImageRequest;
import com.ecommerce.backend.repository.jpa.ItemImageRepository;
import com.ecommerce.backend.service.upload.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemImageCommandService {
    private final ItemImageRepository itemImageRepository;

    private final FileUploadService fileUploadService;

    public void add(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        addOriginalImage(request, multipartFile, item);
        addThumbnailImage(request, multipartFile, item);
    }

    // 원본 파일 저장
    private void addOriginalImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        final ItemImage itemImage = fileUploadService.addOriginalImage(request, multipartFile, item);
        itemImageRepository.save(itemImage);
    }

    // 썸네일 생성 및 저장
    private void addThumbnailImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        // FIXME: extension을 addThumbnailImage 메서드의 인자로 넘기는 것이 좋을까?
        final String extension = StringUtils.getFilenameExtension(request.getOriginImageName());
        final ItemImage itemImage = fileUploadService.addThumbnailImage(request, multipartFile, item, extension);
        itemImageRepository.save(itemImage);
    }
}
