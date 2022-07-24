package com.ecommerce.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component("fileUploadComponent")
public class FileUploadComponent {

    private static final String RANDOM_UUID = String.valueOf(UUID.randomUUID()).substring(0, 13);

    public void uploadFile(String uploadPath, String fileName, byte[] fileData){
        // originFile을 먼저 저장하고
        try(FileOutputStream fos = new FileOutputStream(makeAbsPath(uploadPath, fileName))){
            fos.write(fileData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    // 원래 파일명 + "-" + uuid + 확장자를 통해 copyFileName을 생성한다.
    public String makeCopyFileName(String originFileName){
        return RANDOM_UUID + "-" + originFileName;
    }

    // 생성된 copyFileName을 통해 절대 저장 경로를 생성한다.
    public String makeAbsPath(String uploadPath, String fileName){
        final String fileUploadAbsPath = uploadPath + File.separator + fileName;
        return fileUploadAbsPath;
    }
}
