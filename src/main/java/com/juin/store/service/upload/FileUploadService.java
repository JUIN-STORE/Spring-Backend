package com.juin.store.service.upload;

import com.juin.store.domain.entity.Item;
import com.juin.store.domain.entity.ItemImage;
import com.juin.store.domain.request.ItemImageRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    ItemImage addThumbnailImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item, String extension);

    ItemImage addOriginalImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item);
}

