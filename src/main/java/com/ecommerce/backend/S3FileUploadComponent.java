package com.ecommerce.backend;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileUploadComponent {
    private static final String RANDOM_UUID = String.valueOf(UUID.randomUUID()).substring(0, 13);

    private static final String FOLDER = "item";

    private static final String SLASH = "/";
    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String key = FOLDER + SLASH + makeCopyFileName(file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            final PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket,
                    key,
                    inputStream,
                    objectMetadata);
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicReadWrite);

            s3Client.putObject(putObjectRequest);
        }

        return s3Client.getUrl(bucket, key).toString();
    }

    // 원래 파일명 + "-" + uuid + 확장자를 통해 copyFileName을 생성한다.
    private String makeCopyFileName(String originFileName){
        return RANDOM_UUID + "-" + originFileName;
    }
}
