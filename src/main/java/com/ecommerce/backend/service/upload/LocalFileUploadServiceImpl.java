package com.ecommerce.backend.service.upload;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.request.ItemImageRequest;
import com.ecommerce.backend.utils.FileUploadUtil;
import com.ecommerce.backend.utils.ThumbnailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.ecommerce.backend.utils.FileUploadUtil.*;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "server.env", havingValue = "local", matchIfMissing = false)  // ${server.env} == local이면 빈으로 등록.
public class LocalFileUploadServiceImpl implements FileUploadService {
    @Value("${item-image.original-path}")
    private String itemImageOriginalPath;

    @Value("${item-image.thumbnail-path}")
    private String itemImageThumbnailPath;

    @Override
    public ItemImage addThumbnailImage(ItemImageRequest.Create request
                                     , MultipartFile multipartFile
                                     , Item item
                                     , String extension) {
        createDirectoryIfNotExists(itemImageThumbnailPath);

        final String originalFileName = request.getOriginImageName();
        final String thumbnailFileName = FileUploadUtil.makeThumbnailFileName(originalFileName, THUMB_400);
        final String imageAbsUrl = FileUploadUtil.makeAbsPath(itemImageThumbnailPath, thumbnailFileName);

        try {
            // 썸네일 생성하기
            final BufferedImage resize = ThumbnailUtil.resize(multipartFile, THUMB_400, THUMB_400);
            ImageIO.write(resize, extension, new File(imageAbsUrl));

        } catch (IOException e) {
            log.error("[P1][SRV][IICM][THUM] 로컬에서 썸네일 만드는데 실패하였습니다. message=({})", e.getMessage());
        }

        return request.toItemImage(item, thumbnailFileName, imageAbsUrl, true);
    }

    @Override
    public ItemImage addOriginalImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        createDirectoryIfNotExists(itemImageOriginalPath);

        final String originalFileName = multipartFile.getOriginalFilename();
        final String uuidFileName = FileUploadUtil.makeFileNameWithUuid(originalFileName);
        final String imageAbsUrl = FileUploadUtil.makeAbsPath(itemImageOriginalPath, uuidFileName);

        uploadFile(itemImageOriginalPath, originalFileName, multipartFile);   // 원본

        return request.toItemImage(item, uuidFileName, imageAbsUrl, false);
    }

    private void uploadFile(String uploadPath, String fileName, MultipartFile multipartFile) {
        // originFile을 먼저 저장하고
        try(FileOutputStream fos = new FileOutputStream(makeAbsPath(uploadPath, fileName))){
            fos.write(multipartFile.getBytes());
        } catch (FileNotFoundException e) {
            log.error("[P1][UTIL][FILE][UPAD]: 파일을 찾을 수 없습니다. message=({})", e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("[P1][UTIL][FILE][UPAD]: IOException message=({})", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
