package store.juin.api.upload.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import store.juin.api.common.exception.JUINIOException;
import store.juin.api.item.model.entity.Item;
import store.juin.api.itemcategory.model.request.ItemImageCreateRequest;
import store.juin.api.itemimage.model.entity.ItemImage;
import store.juin.api.upload.service.FileUploadService;
import store.juin.api.util.FileUploadUtil;
import store.juin.api.util.ThumbnailUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static store.juin.api.util.FileUploadUtil.*;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "item-image.local-path-active", havingValue = "true", matchIfMissing = false) // ${item-image.local-path-active} == true일 때 빈 등록.
public class LocalFileUploadServiceImpl implements FileUploadService {
    @Value("${item-image.original-path}")
    private String itemImageOriginalPath;

    @Value("${item-image.thumbnail-path}")
    private String itemImageThumbnailPath;

    @Override
    public ItemImage addThumbnailImage(ItemImageCreateRequest request, MultipartFile multipartFile, Item item, String extension) {
        createDirectoryIfNotExists(itemImageThumbnailPath);

        final String originalFileName = request.getOriginImageName();
        final String thumbnailFileName = FileUploadUtil.makeThumbnailFileName(originalFileName, THUMB_400);
        final String imageAbsUrl = FileUploadUtil.makeAbsPath(itemImageThumbnailPath, thumbnailFileName);

        try {
            // 썸네일 생성하기
            final BufferedImage resize = ThumbnailUtil.resize(multipartFile, THUMB_400, THUMB_400);
            ImageIO.write(resize, extension, new File(imageAbsUrl));

        } catch (IOException e) {
            log.error("[P1][SERV][IICM][THUM] 로컬에서 썸네일 만드는데 실패하였습니다. message=({})", e.getMessage());
        }

        return request.toItemImage(item, thumbnailFileName, imageAbsUrl, true);
    }

    @Override
    public ItemImage addOriginalImage(ItemImageCreateRequest request, MultipartFile multipartFile, Item item) {
        createDirectoryIfNotExists(itemImageOriginalPath);

        final String originalFileName = multipartFile.getOriginalFilename();
        final String uuidFileName = FileUploadUtil.makeFileNameWithUuid(originalFileName);
        final String imageAbsUrl = FileUploadUtil.makeAbsPath(itemImageOriginalPath, uuidFileName);

        uploadFile(itemImageOriginalPath, uuidFileName, multipartFile);   // 원본

        return request.toItemImage(item, uuidFileName, imageAbsUrl, false);
    }

    private void uploadFile(String uploadPath, String fileName, MultipartFile multipartFile) {
        // originFile을 먼저 저장하고
        try(FileOutputStream fos = new FileOutputStream(makeAbsPath(uploadPath, fileName))){
            fos.write(multipartFile.getBytes());
        } catch (FileNotFoundException e) {
            log.error("[P1][UTIL][FILE][UPAD]: 파일을 찾을 수 없습니다. message=({})", e.getMessage());
            throw new JUINIOException(e);
        } catch (IOException e) {
            log.error("[P1][UTIL][FILE][UPAD]: IOException message=({})", e.getMessage());
            throw new JUINIOException(e);
        }
    }
}
