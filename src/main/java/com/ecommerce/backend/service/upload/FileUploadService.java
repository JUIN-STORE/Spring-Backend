package com.ecommerce.backend.service.upload;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.request.ItemImageRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    ItemImage addThumbnailImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item, String extension);

    ItemImage addOriginalImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item);
}

