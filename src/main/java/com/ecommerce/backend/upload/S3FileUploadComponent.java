package com.ecommerce.backend.upload;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ecommerce.backend.utils.CharterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileUploadComponent {
    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.directory}")
    private String directory;

    public String uploadFile(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String key = directory + CharterUtil.SLASH + FileUpload.makeCopyFileName(file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            final PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket,
                    key,
                    inputStream,
                    objectMetadata);
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicReadWrite);

            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.warn("[P5][COM][S3FU][UPLD]: getInputStream()에서 Exception=({})이 발생했습니다.", e.getMessage());
        }

        return s3Client.getUrl(bucket, key).toString();
    }

}
