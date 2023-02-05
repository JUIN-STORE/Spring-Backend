package com.juin.store.utils;

import com.juin.store.exception.JUINIOException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Slf4j
@UtilityClass
public class ThumbnailUtil {
    public static BufferedImage resize(MultipartFile multipartFile, int width, int height) {
        try {
            BufferedImage inputImage = ImageIO.read(multipartFile.getInputStream());
            BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

            Graphics2D graphics2D = outputImage.createGraphics();
            graphics2D.drawImage(inputImage, 0, 0, width, height, null);
            graphics2D.dispose();

            return outputImage;
        } catch (IOException e) {
            log.error("[P1][UTIL][THMB][RESZ]: 리사이징 중 에러가 발생했습니다. message=({})", e.getMessage(), e);
            throw new JUINIOException(e);
        }
    }
}