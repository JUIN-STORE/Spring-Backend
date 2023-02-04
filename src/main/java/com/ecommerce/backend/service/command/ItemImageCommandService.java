package com.ecommerce.backend.service.command;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.request.ItemImageRequest;
import com.ecommerce.backend.repository.jpa.ItemImageRepository;
import com.ecommerce.backend.upload.FileUpload;
import com.ecommerce.backend.upload.S3FileUploadComponent;
import com.ecommerce.backend.utils.ThumbnailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.ecommerce.backend.upload.FileUpload.createDirectoryIfNotExists;
import static com.ecommerce.backend.upload.FileUpload.deleteIfExists;

// FIXME: 기능 정상화되면 추상화해서 local, s3 분리
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemImageCommandService {
    private final ItemImageRepository itemImageRepository;

    private final S3FileUploadComponent s3FileUploadComponent;

    @Value("${item-image.original-path:#{null}}")
    private String itemImageOriginalPath;

    @Value("${item-image.thumbnail-path:#{null}}")
    private String itemImageThumbnailPath;

    @Value("${item-image.resize-path:#{null}}")
    private String resizePath;

    private static final int THUMBNAIL_SIZE = 400;

    public void add(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        addOriginalImage(request, multipartFile, item);
        addThumbnailImage(request, multipartFile, item);
    }

    // 원본 파일 저장
    private void addOriginalImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        if (StringUtils.hasText(itemImageOriginalPath)) {
            createDirectoryIfNotExists(itemImageOriginalPath);
            addOriginalImageAtLocal(request, multipartFile, item);
        } else {
            addOriginalImageAtS3(request, multipartFile, item);
        }
    }

    // 썸네일 생성 및 저장
    private void addThumbnailImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        final String extension = StringUtils.getFilenameExtension(request.getOriginImageName());

        if (StringUtils.hasText(itemImageThumbnailPath)) {
            createDirectoryIfNotExists(itemImageThumbnailPath);
            addThumbnailImageAtLocal(request, multipartFile, item, extension);
        } else {
            addThumbnailImageAtS3(request, multipartFile, item, extension);
        }
    }

    // 로컬에 업로드
    private void addThumbnailImageAtLocal(ItemImageRequest.Create request, MultipartFile multipartFile, Item item, String extension) {
        final String originalFileName = request.getOriginImageName();
        final String thumbnailFileName = FileUpload.makeThumbnailFileName(originalFileName, THUMBNAIL_SIZE);
        final String imageAbsUrl = FileUpload.makeAbsPath(itemImageThumbnailPath, thumbnailFileName);

        try {
            // 썸네일 생성하기
            final BufferedImage resize = ThumbnailUtil.resize(multipartFile, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
            ImageIO.write(resize, extension, new File(imageAbsUrl));

        } catch (IOException e) {
            log.error("[P1][SRV][IICM][THUM] 로컬에서 썸네일 만드는데 실패하였습니다. message=({})", e.getMessage());
        }

        final ItemImage itemImage = request.toItemImage(item, thumbnailFileName, imageAbsUrl, true);

        itemImageRepository.save(itemImage);
    }

    private void addOriginalImageAtLocal(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        final String originalFileName = multipartFile.getOriginalFilename();
        final String uuidFileName = FileUpload.makeFileNameWithUuid(originalFileName);
        final String imageAbsUrl = FileUpload.makeAbsPath(itemImageOriginalPath, uuidFileName);

        FileUpload.uploadFile(itemImageOriginalPath, originalFileName, multipartFile);   // 원본

        final ItemImage itemImage = request.toItemImage(item, uuidFileName, imageAbsUrl, false);

        itemImageRepository.save(itemImage);
    }


    // S3 업로드
    private void addThumbnailImageAtS3(ItemImageRequest.Create request,
                                       MultipartFile multipartFile,
                                       Item item,
                                       String extension) {
        try {
            // 썸네일 생성하기
            final BufferedImage resize = ThumbnailUtil.resize(multipartFile, THUMBNAIL_SIZE, THUMBNAIL_SIZE);

            createDirectoryIfNotExists(resizePath);
            final String path = FileUpload.makeAbsPath(resizePath, request.getOriginImageName());

            final File file = new File(path);
            ImageIO.write(resize, extension, file);

            final String uploadFileUrl = s3FileUploadComponent.uploadFile("thumbnail", file);

            final ItemImage itemImage = request.toItemImage(item, multipartFile.getOriginalFilename(), uploadFileUrl, true);
            itemImageRepository.save(itemImage);
            deleteIfExists(file);
        } catch (IOException e) {
            log.error("[P1][SRV][IICM][THUM]: s3에 썸네일을 저장하는데 실패하였습니다. message=({})", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void addOriginalImageAtS3(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        final File file = FileUpload.convertMultipartFileToFile(multipartFile, resizePath);

        final String uploadFileUrl = s3FileUploadComponent.uploadFile("original", file);
        final String imageName = FileUpload.makeThumbnailFileName(request.getOriginImageName(), THUMBNAIL_SIZE);

        final ItemImage itemImage = request.toItemImage(item, imageName, uploadFileUrl, false);

        itemImageRepository.save(itemImage);
        deleteIfExists(file);
    }
}
