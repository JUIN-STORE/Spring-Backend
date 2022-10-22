package com.ecommerce.backend.service;

import com.ecommerce.backend.FileUploadComponent;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.request.ProductImageRequest;
import com.ecommerce.backend.repository.jpa.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;

/** Service Naming
 * C -> save
 * R -> findBy~
 * U -> update
 * D -> delete
 */

/** Service Rule
 *  Declare only ONE Repository and pull the others from the Service
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    private final FileUploadComponent fileUploadComponent;

    @Value("${productImageLocation}")
    private String productImageLocation;

    public void saveProductImage(ProductImageRequest.Create request, MultipartFile multipartFile, Product product) throws IOException {
        final String originalFilename = multipartFile.getOriginalFilename(); // cat.jpg

        // 파일 업로드
        if(StringUtils.hasText(originalFilename)){
            final String copyFileName = fileUploadComponent.makeCopyFileName(originalFilename);
            final String fileUploadAbsPath = fileUploadComponent.makeAbsPath(productImageLocation, copyFileName);

            fileUploadComponent.uploadFile(productImageLocation, originalFilename, multipartFile.getBytes()); // 원본
            fileUploadComponent.uploadFile(productImageLocation, copyFileName, multipartFile.getBytes());     // copy

            request.setImageName(copyFileName);
            request.setImageUrl(fileUploadAbsPath);
            request.setOriginImageName(originalFilename);

            final ProductImage productImage = request.toProductImage(product);

            productImageRepository.save(productImage);
        }
    }

    @Transactional(readOnly = true)
    public List<ProductImage> findById(Long productId){
        // 상품이미지 조회
        return productImageRepository.findByProductId(productId);
    }

    @Transactional()
    public void delete(Long productId) {
        final List<ProductImage> productImageList = this.findById(productId);
        productImageRepository.deleteAll(productImageList);
    }

    public List<ProductImage> readAllByProductId(List<Long> productIdList) {
        return productImageRepository.findAllByProductId(productIdList)
                .orElseThrow(EntityNotFoundException::new);
    }
}
