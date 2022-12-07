package com.ecommerce.backend.service.relation;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.ecommerce.backend.service.ProductImageService;
import com.ecommerce.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRelationService {
    private final ProductService productService;
    private final ProductImageService productImageService;

    public List<Product> getProductList(List<Long> productIdList) {
        return productService.readByIdList(productIdList);
    }

    public List<ProductImage> getThumbnailProductImageList(boolean isThumbnail) {
        return productImageService.readByThumbnail(isThumbnail);
    }


    public List<CartProductResponse.Read> getCartProductReadResponse(List<Long> productIdList) {
        return productService.readAllByProductIdAndProductImageIdAndThumbnail(productIdList);
    }
}