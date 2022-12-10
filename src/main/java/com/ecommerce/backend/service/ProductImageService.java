package com.ecommerce.backend.service;

import com.ecommerce.backend.FileUploadComponent;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.request.ProductImageRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    private final FileUploadComponent fileUploadComponent;

    @Value("${product-image-location}")
    private String productImageLocation;

    public void add(ProductImageRequest.Create request, MultipartFile multipartFile, Product product) throws IOException {
        final String originImageName = multipartFile.getOriginalFilename(); // cat.jpg

        // 파일 업로드
        if(StringUtils.hasText(originImageName)){
            final String copyImageName = fileUploadComponent.makeCopyFileName(originImageName);
            final String imageAbsUrl = fileUploadComponent.makeAbsPath(productImageLocation, copyImageName);

            fileUploadComponent.uploadFile(productImageLocation, originImageName, multipartFile.getBytes());   // 원본
            fileUploadComponent.uploadFile(productImageLocation, copyImageName, multipartFile.getBytes());     // copy

            final ProductImage productImage =
                    request.toProductImage(product, copyImageName, imageAbsUrl, originImageName);

            productImageRepository.save(productImage);
        }
    }


    public List<ProductImage> readAllByThumbnail(boolean isThumbnail) {
        return productImageRepository.findByThumbnail(isThumbnail)
                .orElseThrow(() -> new EntityNotFoundException(Msg.PRODUCT_THUMBNAIL_NOT_FOUND));
    }

    public List<ProductImage> readAllByProductId(List<Long> productIdList) {
        return productImageRepository.findAllByProductIdIn(productIdList)
                .orElse(new ArrayList<>());
    }
}
