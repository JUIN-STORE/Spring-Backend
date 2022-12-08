package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.enums.ProductStatus;
import com.ecommerce.backend.domain.request.ProductImageRequest;
import com.ecommerce.backend.domain.request.ProductRequest;
import com.ecommerce.backend.repository.jpa.ProductRepository;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductImageService productImageService;
    private final CategoryService categoryService;
    private final ProductCategoryService productCategoryService;

    @Transactional
    public Long add(ProductRequest.Create request,
                    MultipartFile thumbnailImage,
                    List<MultipartFile> productImageFileList) throws IOException {
        if (thumbnailImage == null) throw new InvalidParameterException(Msg.PRODUCT_THUMBNAIL_IMAGE_REQUIRED);

        // 상품 등록
        final Category category = categoryService.readById(request.getCategoryId());
        final Product product = request.toProduct(category);

        productCategoryService.add(product, category);
        productRepository.save(product);

        // 썸네일 등록
        productImageService.saveProductImage(new ProductImageRequest.Create().setThumbnail(true), thumbnailImage, product);

        // 썸네일 외 이미지 등록
        if (!Collections.isEmpty(productImageFileList)) {
            for (MultipartFile productImageFile : productImageFileList) {
                productImageService.saveProductImage(new ProductImageRequest.Create().setThumbnail(false), productImageFile, product);
            }
        }

        return product.getId();
    }

    public Product readByProductId(Long productId){
        return productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Product> readByIdList(List<Long> productId){
        return productRepository.findByIdIn(productId)
                .orElse(new ArrayList<>());
    }

    @Transactional(rollbackFor = Exception.class)
    public Long remove(Long productId){
        final Product product = this.readByProductId(productId);
        product.updateStatus(ProductStatus.SOLD_OUT);

        return product.getId();
    }

    public Page<Product> read(Pageable pageable, Long categoryId) {
        if (categoryId == null) return productRepository.findAll(pageable);

        return productRepository.findByCategoryId(pageable, categoryId);
    }

    public Long readCount(){
        return productRepository.count();
    }

    public Page<Product> search(Pageable pageable, String searchTitle, Long categoryId) {
        if (categoryId == null) return productRepository.findByProductNameContaining(pageable, searchTitle);
        return productRepository.findByProductNameContainingAndCategoryId(pageable, searchTitle, categoryId);
    }

    public Long readSearchCount(String searchTitle){
        return productRepository.countByProductNameContaining(searchTitle);
    }
}