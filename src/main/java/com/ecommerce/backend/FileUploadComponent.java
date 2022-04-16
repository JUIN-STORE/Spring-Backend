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
    public void uploadFile(String uploadPath, String originFileName, byte[] fileData){
        try(FileOutputStream fos = new FileOutputStream(makeAbsPath(uploadPath, originFileName))){
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

    // uuid를 생성한다.
    public String makeUUID() {
        String uuid = String.valueOf(UUID.randomUUID());
        return uuid.substring(0, 13);
    }

    // 원래 파일명 + "-" + uuid + 확장자를 통해 copyFileName을 생성한다.
    public String makeCopyFileName(String originFileName){
        return makeUUID() + "-" + originFileName;
    }

    // 생성된 copyFileName을 통해 절대 저장 경로를 생성한다.
    public String makeAbsPath(String uploadPath, String originFileName){
        final String fileUploadAbsPath = uploadPath + File.separator + makeCopyFileName(originFileName);
        return fileUploadAbsPath;
    }
}
