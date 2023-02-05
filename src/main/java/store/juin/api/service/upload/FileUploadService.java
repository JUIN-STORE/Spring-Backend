package store.juin.api.service.upload;

import store.juin.api.domain.entity.Item;
import store.juin.api.domain.entity.ItemImage;
import store.juin.api.domain.request.ItemImageRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    ItemImage addThumbnailImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item, String extension);

    ItemImage addOriginalImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item);
}

