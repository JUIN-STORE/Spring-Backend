package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    void createProduct() {
        Product product = Product.builder()
                .productName("테스트 상품1")
                .price(10000)
                .quantity(100)
                .soldCount(1)
                .description("테스트 상품 설명")
                .thumbnailPath("/url")
                .originImagePath("/origin-url")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Product save = productRepository.save(product);
        System.out.println(save);
    }
}