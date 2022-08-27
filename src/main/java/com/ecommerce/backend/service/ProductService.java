package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.request.ProductImageRequest;
import com.ecommerce.backend.domain.request.ProductRequest;
import com.ecommerce.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Collections;
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
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductImageService productImageService;

    public Long add(ProductRequest.Create request, List<MultipartFile> productImageFileList) throws IOException {
        // 상품 등록
        final Product product = request.toProduct();
        productRepository.save(product);

        boolean isThumbnail;

        // 상품 이미지 등록
        for (int i = 0; i < productImageFileList.size(); i++){
            ProductImageRequest.Create createRequest = new ProductImageRequest.Create();

            if (i == 0) isThumbnail = true;
            else isThumbnail = false;

            createRequest.setThumbnail(isThumbnail);
            productImageService.saveProductImage(createRequest, productImageFileList.get(i), product);
        }
        return product.getId();
    }

    @Transactional(readOnly = true)
    public Product readByProductId(Long productId){
        return productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Product> readByIdList(List<Long> productId){
        final List<Product> productList = productRepository.findByIdList(productId);

        if (CollectionUtils.isEmpty(productList)) return Collections.emptyList();

        return productList;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long remove(Long productId){
        final Product product = this.readByProductId(productId);

        productImageService.delete(productId);
        productRepository.delete(product);
        return product.getId();
    }

    public Page<Product> readAll(Pageable pageable){
        return productRepository.findAll(pageable);
    }

    public Long readCount(){
        return productRepository.count();
    }

    public Page<Product> search(Pageable pageable, String searchTitle) {
        return productRepository.findByProductNameContaining(pageable, searchTitle);
    }

    public Long readSearchCount(String searchTitle){
        return productRepository.countByProductNameContaining(searchTitle);
    }
}