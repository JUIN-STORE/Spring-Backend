package com.ecommerce.backend.upload;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ecommerce.backend.utils.CharterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileUploadComponent {
    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.directory}")
    private String directory;

    private static final int THUMB_SIZE = 400;

    public String uploadFile(String subDirectory, File file) {
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
                + FileUpload.makeThumbnailFileName(fileName, THUMB_SIZE);
    }
}
