package com.ecommerce.backend.upload;

import com.ecommerce.backend.utils.CharterUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public final class FileUpload {
    public static void uploadFile(String uploadPath, String fileName, byte[] fileData) {
        // originFile을 먼저 저장하고
        try(FileOutputStream fos = new FileOutputStream(makeAbsPath(uploadPath, fileName))){
            fos.write(fileData);
        } catch (FileNotFoundException e) {
            log.error("[P1][UTIL][FILE][UPAD]: 파일을 찾을 수 없습니다. message=({})", e.getMessage());
        } catch (IOException e) {
            log.error("[P1][UTIL][FILE][UPAD]: IOException message=({})", e.getMessage());
        }
    }

    // 원래 파일명 + "-" + uuid + 확장자를 통해 copyFileName을 생성한다.
    public static String makeCopyFileName(String originalFileName){
        final String RANDOM_UUID = String.valueOf(UUID.randomUUID()).substring(0, 13);

        return RANDOM_UUID + CharterUtil.DASH + originalFileName;
    }

    public static String makeAbsPath(String uploadPath, String fileName){
        return uploadPath + CharterUtil.SLASH + fileName;
    }


    public void deleteFile(String filePath){
        final File deleteFile = new File(filePath);

        if (deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일 삭제 성공");
        } else {
            log.warn("파일 삭제 실패");
        }
    }
}
