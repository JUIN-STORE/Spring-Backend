package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.request.ProductRequest;
import com.ecommerce.backend.repository.ProductImageRepository;
import com.ecommerce.backend.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *  테스트 메서드는 의도적으로 스네이크 케이스로 작성
 */

@SpringBootTest
class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;


    List<MultipartFile> createMultipartFileList() throws IOException {
        List<MultipartFile> multipartFileList = new ArrayList<>();

        for (int i = 0; i < 5; i++){
             String filePath = "C:/shop/product/";
             String productName = "cat" + i + ".jpg";
             MockMultipartFile mockMultipartFile = new MockMultipartFile(filePath, productName, "image/jpg", new byte[]{1, 2, 3, 4, 5});
            multipartFileList.add(mockMultipartFile);
        }

        return multipartFileList;
    }

    @Test
    @Transactional
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void product_register_test() throws IOException {
        // given, when
        final ProductRequest.Create request = new ProductRequest.Create();
        request.setProductName("cat0.jpg");
        request.setPrice(10000);
        request.setQuantity(100);
        request.setQuantity(100);
        request.setDescription("testDescription");

        final List<MultipartFile> multipartFileList = createMultipartFileList();
        final Long productId = productService.add(request, multipartFileList);

        List<ProductImage> productImageList = productImageRepository.findByProductId(productId);

        final Product product = productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);

        // then
        assertEquals(request.getProductName(), product.getProductName());
        assertEquals(request.getDescription(), product.getDescription());
        assertEquals(request.getPrice(), product.getPrice());
        assertEquals(request.getQuantity(), product.getQuantity());
        assertEquals(multipartFileList.get(0).getOriginalFilename(), productImageList.get(0).getOriginImageName());
    }
}