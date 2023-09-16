package store.juin.api.upload.service;

import org.springframework.web.multipart.MultipartFile;
import store.juin.api.item.model.entity.Item;
import store.juin.api.itemcategory.model.request.ItemImageRequest;
import store.juin.api.itemimage.model.entity.ItemImage;

public interface FileUploadService {
    ItemImage addThumbnailImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item, String extension);

    ItemImage addOriginalImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item);
}

