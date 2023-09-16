package store.juin.api.upload.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import store.juin.api.common.exception.JUINIOException;
import store.juin.api.item.model.entity.Item;
import store.juin.api.itemcategory.model.request.ItemImageRequest;
import store.juin.api.itemimage.model.entity.ItemImage;
import store.juin.api.upload.service.FileUploadService;
import store.juin.api.util.CharterUtil;
import store.juin.api.util.FileUploadUtil;
import store.juin.api.util.ThumbnailUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static store.juin.api.util.FileUploadUtil.*;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "item-image.local-path-active", havingValue = "false", matchIfMissing = false) // ${item-image.local-path-active} == false일 때 빈 등록.
public class S3FileUploadServiceImpl implements FileUploadService {
    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.directory}")
    private String directory;

    @Value("${item-image.resize-path}")
    private String resizePath;

    @Override
    public ItemImage addThumbnailImage(ItemImageRequest.Create request
                                     , MultipartFile multipartFile
                                     , Item item
                                     , String extension) {
        try {
            // 썸네일 생성하기
            final BufferedImage resize = ThumbnailUtil.resize(multipartFile, THUMB_400, THUMB_400);

            createDirectoryIfNotExists(resizePath);
            final String path = FileUploadUtil.makeAbsPath(resizePath, request.getOriginImageName());

            final File file = new File(path);
            ImageIO.write(resize, extension, file);

            final String uploadFileUrl = uploadFile("thumbnail", file);

            deleteIfExists(file);
            return request.toItemImage(item, multipartFile.getOriginalFilename(), uploadFileUrl, true);
        } catch (IOException e) {
            log.error("[P1][SERV][IICM][THUM]: s3에 썸네일을 저장하는데 실패하였습니다. message=({})", e.getMessage());
            throw new JUINIOException(e);
        }
    }

    @Override
    public ItemImage addOriginalImage(ItemImageRequest.Create request, MultipartFile multipartFile, Item item) {
        final File file = FileUploadUtil.convertMultipartFileToFile(multipartFile, resizePath);

        final String uploadFileUrl = uploadFile("original", file);
        final String imageName = FileUploadUtil.makeThumbnailFileName(request.getOriginImageName(), THUMB_400);

        deleteIfExists(file);
        return request.toItemImage(item, imageName, uploadFileUrl, false);
    }

    private String uploadFile(String subDirectory, File file) {
        String key = makeKey(subDirectory, file.getName());

        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file);
        putObjectRequest.withCannedAcl(CannedAccessControlList.PublicReadWrite);

        s3Client.putObject(putObjectRequest);

        return s3Client.getUrl(bucket, key).toString();
    }

    private String makeKey(String subdirectory, String fileName) {
        return directory
                + CharterUtil.SLASH
                + subdirectory
                + CharterUtil.SLASH
                + FileUploadUtil.makeThumbnailFileName(fileName, THUMB_400);
    }

}