package com.ecommerce.backend.service;

import com.ecommerce.backend.FileUploadComponent;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.request.ItemImageRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemImageService {
    private final ItemImageRepository itemImageRepository;

    private final FileUploadComponent fileUploadComponent;

    @Value("${item-image-location}")
    private String itemImageLocation;

    public void add(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) throws IOException {
        final String originName = multipartFile.getOriginalFilename(); // cat.jpg

        // 파일 업로드
        if(StringUtils.hasText(originName)){
            final String copyImageName = fileUploadComponent.makeCopyFileName(originName);
            final String imageAbsUrl = fileUploadComponent.makeAbsPath(itemImageLocation, copyImageName);

            fileUploadComponent.uploadFile(itemImageLocation, originName, multipartFile.getBytes());   // 원본
            fileUploadComponent.uploadFile(itemImageLocation, copyImageName, multipartFile.getBytes());     // copy

            final ItemImage itemImage =
                    request.toItemImage(item, copyImageName, imageAbsUrl, originName);

            itemImageRepository.save(itemImage);
        }
    }


    public List<ItemImage> readAllByThumbnail(boolean isThumbnail) {
        return itemImageRepository.findByThumbnail(isThumbnail)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_THUMBNAIL_NOT_FOUND));
    }

    public List<ItemImage> readAllByItemId(List<Long> itemIdList) {
        return itemImageRepository.findAllByItemIdIn(itemIdList)
                .orElse(new ArrayList<>());
    }
}
