package com.ecommerce.backend.service.relation;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.response.ProductResponse;
import com.ecommerce.backend.service.ProductImageService;
import com.ecommerce.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<ProductResponse.Read> read(Pageable pageable, Long categoryId) {
        Page<Product> productList = productService.read(pageable, categoryId);
        return getResponse(productList);
    }

    public List<ProductResponse.Read> search(Pageable pageable, String searchTitle, Long categoryId) {
        Page<Product> productList = productService.search(pageable, searchTitle, categoryId);
        return getResponse(productList);
    }

    private List<ProductResponse.Read> getResponse(Page<Product> productList) {
        final List<Long> productIdList = productList.stream().map(Product::getId).collect(Collectors.toList());
        final List<ProductImage> productImageList = productImageService.readAllByProductId(productIdList);
        final Map<Long, List<ProductImage>> productIdImageMap = new HashMap<>();

        for (ProductImage productImage : productImageList) {
            Product product = productImage.getProduct();
            if (product == null) continue;

            Long productId = product.getId();
            List<ProductImage> imageListInProduct = productIdImageMap.getOrDefault(productId, new ArrayList<>());
            imageListInProduct.add(productImage);
            productIdImageMap.put(productId, imageListInProduct);
        }

        return productList.stream()
                .map(image -> ProductResponse.Read.of(image, productIdImageMap.get(image.getId())))
                .collect(Collectors.toList());
    }
}