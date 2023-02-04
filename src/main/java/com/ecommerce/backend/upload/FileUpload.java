package com.ecommerce.backend.upload;

import com.ecommerce.backend.utils.CharterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
public final class FileUpload {
    public static void uploadFile(String uploadPath, String fileName, MultipartFile multipartFile) {
        // originFile을 먼저 저장하고
        try(FileOutputStream fos = new FileOutputStream(makeAbsPath(uploadPath, fileName))){
            fos.write(multipartFile.getBytes());
        } catch (FileNotFoundException e) {
            log.error("[P1][UTIL][FILE][UPAD]: 파일을 찾을 수 없습니다. message=({})", e.getMessage());
        } catch (IOException e) {
            log.error("[P1][UTIL][FILE][UPAD]: IOException message=({})", e.getMessage());
        }
    }

    // 원래 파일명 + "-" + uuid + 확장자를 통해 copyFileName을 생성한다.
    public static String makeFileNameWithUuid(String originalFileName){
        final String uuid = String.valueOf(UUID.randomUUID()).substring(0, 8);

        return uuid + CharterUtil.DASH + originalFileName;
    }

    public static String makeAbsPath(String uploadPath, String fileName){
        return uploadPath + CharterUtil.SLASH + fileName;
    }

    public static String makeThumbnailFileName(String originalFileName, int size){
        final String uuid = String.valueOf(UUID.randomUUID()).substring(0, 13);

        final String[] split = originalFileName.split("\\.");
        final String fileName = split[0];
        final String extension = split[1];

        return uuid + CharterUtil.DASH + fileName + CharterUtil.UNDER_BAR + size + CharterUtil.DOT + extension;
    }

    public static void createDirectoryIfNotExists(String path) {
        try {
            File file = new File(path);
            if (file.exists()) return;
            Files.createDirectories(file.toPath());
        } catch (IOException e) {
            log.error("폴더 생성에 실패하였습니다.");
            throw new RuntimeException(e);
        }
    }
    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
            log.info("파일 삭제 성공");
        } else {
            log.warn("파일 삭제 실패");
        }
    }
}
