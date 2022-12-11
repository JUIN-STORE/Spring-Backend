package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.enums.ProductStatus;
import com.ecommerce.backend.domain.request.ProductImageRequest;
import com.ecommerce.backend.domain.request.ProductRequest;
import com.ecommerce.backend.exception.Msg;
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
        if (thumbnailImage == null) throw new InvalidParameterException(Msg.PRODUCT_THUMBNAIL_REQUIRED);

        // 상품 등록
        final Category category = categoryService.readById(request.getCategoryId());
        final Product product = request.toProduct(category);

        productCategoryService.add(product, category);
        productRepository.save(product);

        // 썸네일 등록
        productImageService.add(new ProductImageRequest.Create(true), thumbnailImage, product);

        final Long productId = product.getId();

        // 썸네일 외 이미지 없으면 리턴
        if (Collections.isEmpty(productImageFileList)) return productId;

        // 썸네일 외 이미지 등록
        for (MultipartFile productImageFile : productImageFileList) {
            productImageService.add(new ProductImageRequest.Create(false), productImageFile, product);
        }

        return productId;
    }


    public Product readById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.PRODUCT_NOT_FOUND));
    }

    public List<Product> readAllByIdList(List<Long> productIdList) {
        return productRepository.findAllByIdIn(productIdList)
                .orElseThrow(() -> new EntityNotFoundException(Msg.PRODUCT_NOT_FOUND));
    }

    public Page<Product> readAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> readAllByCategoryId(Pageable pageable, Long categoryId) {
        return productRepository.findAllByCategoryId(pageable, categoryId);
    }

    public Page<Product> readAllByProductNameContaining(Pageable pageable, String searchTitle) {
        return productRepository.findAllByProductNameContaining(pageable, searchTitle);
    }

    public Page<Product>
    readAllByProductNameContainingAndCategoryId(Pageable pageable, String searchTitle, Long categoryId) {

        return productRepository.findAllByProductNameContainingAndCategoryId(pageable, searchTitle, categoryId);
    }

    public long total() {
        return productRepository.count();
    }

    public Long totalByProductNameContaining(String searchTitle) {
        return productRepository.countByProductNameContaining(searchTitle);
    }


    @Transactional(rollbackFor = Exception.class)
    public Long remove(Long productId) {
        final Product product = this.readById(productId);
        product.updateStatus(ProductStatus.SOLD_OUT);

        return product.getId();
    }
}