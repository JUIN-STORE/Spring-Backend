package com.ecommerce.backend.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@UtilityClass
public class ThumbnailUtil {
    public static BufferedImage resize(InputStream inputStream, int width, int height) {
        try {
            BufferedImage inputImage = ImageIO.read(inputStream);
            BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

            Graphics2D graphics2D = outputImage.createGraphics();
            graphics2D.drawImage(inputImage, 0, 0, width, height, null);
            graphics2D.dispose();

            return outputImage;
        } catch (IOException e) {
            log.error("Error resizing image", e);
            throw new RuntimeException(e);
        }
    }
}