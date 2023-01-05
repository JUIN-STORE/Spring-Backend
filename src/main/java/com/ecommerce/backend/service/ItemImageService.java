package com.ecommerce.backend.service;

import com.ecommerce.backend.FileUploadComponent;
import com.ecommerce.backend.S3FileUploadComponent;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.request.ItemImageRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemImageService {
    private final ItemImageRepository itemImageRepository;

    private final FileUploadComponent fileUploadComponent;

    private final S3FileUploadComponent s3FileUploadComponent;

    public void add(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) throws IOException {
        final String originalFileName = multipartFile.getOriginalFilename(); // cat.jpg

        if (!StringUtils.hasText(originalFileName)) throw new InvalidParameterException();

        final String uploadFileUrl = s3FileUploadComponent.uploadFile(multipartFile);
        final String copyImageName = Paths.get(uploadFileUrl).getFileName().toString();

        final ItemImage itemImage = request.toItemImage(item, copyImageName, uploadFileUrl, originalFileName);
        itemImageRepository.save(itemImage);
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
