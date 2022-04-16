package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.request.ProductImageRequest;
import com.ecommerce.backend.domain.request.ProductRequest;
import com.ecommerce.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;

    public Long saveProduct(ProductRequest.CreateRequest request, List<MultipartFile> productImageFileList) throws IOException {
        // 상품 등록
        final Product product = request.toProduct();
        productRepository.save(product);

        // 상품 이미지 등록
        for (int i = 0; i < productImageFileList.size(); i++){
            ProductImageRequest.CreateRequest createRequest = new ProductImageRequest.CreateRequest();

            if (i == 0) {
                createRequest.setThumbnail(true);
            }
            else {
                createRequest.setThumbnail(false);
            }
            productImageService.saveProductImage(createRequest, productImageFileList.get(i), product);
        }
        return product.getId();
    }
}
