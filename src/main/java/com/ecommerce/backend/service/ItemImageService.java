package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.request.ItemImageRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ItemImageRepository;
import com.ecommerce.backend.upload.FileUpload;
import com.ecommerce.backend.upload.S3FileUploadComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemImageService {
    private final ItemImageRepository itemImageRepository;

    private final S3FileUploadComponent s3FileUploadComponent;

    @Value("${item-image-location}")
    private String itemImageLocation;

    public List<ItemImage> readAllByThumbnail(boolean isThumbnail) {
        return itemImageRepository.findByThumbnail(isThumbnail)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_THUMBNAIL_NOT_FOUND));
    }

    public List<ItemImage> readAllByItemId(List<Long> itemIdList) {
        return itemImageRepository.findAllByItemIdIn(itemIdList)
                .orElse(new ArrayList<>());
    }


    public void add(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        final String originalFileName = multipartFile.getOriginalFilename(); // cat.jpg
        if (!StringUtils.hasText(originalFileName)) throw new InvalidParameterException();

        if (StringUtils.hasText(itemImageLocation)) {
            localAdd(request, multipartFile, item, originalFileName);
        } else {
            s3Add(request, multipartFile, item, originalFileName);
        }
    }

    private void localAdd(ItemImageRequest.Create request, MultipartFile multipartFile, Item item, String originalFileName) {
        final String copyImageName = FileUpload.makeCopyFileName(originalFileName);
        final String imageAbsUrl = FileUpload.makeAbsPath(itemImageLocation, copyImageName);

        try {
            final byte[] bytes = multipartFile.getBytes();
            FileUpload.uploadFile(itemImageLocation, originalFileName, bytes);   // 원본
            FileUpload.uploadFile(itemImageLocation, copyImageName, bytes);     // copy

        } catch (Exception e) {
            log.error("ItemImageService.localAdd() multipartFile.getBytes() error: {}", e.getMessage());
        }

        final ItemImage itemImage =
                request.toItemImage(item, copyImageName, imageAbsUrl, originalFileName);

        itemImageRepository.save(itemImage);
    }

    private void s3Add(ItemImageRequest.Create request, MultipartFile multipartFile, Item item, String originalFileName) {
        final String uploadFileUrl = s3FileUploadComponent.uploadFile(multipartFile);
        final String copyImageName = Paths.get(uploadFileUrl).getFileName().toString();

        final ItemImage itemImage = request.toItemImage(item, copyImageName, uploadFileUrl, originalFileName);
        itemImageRepository.save(itemImage);
    }
}
