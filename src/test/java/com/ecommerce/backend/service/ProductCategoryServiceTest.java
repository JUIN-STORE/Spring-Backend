package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.repository.jpa.ProductCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {
    @Mock
    private ProductCategoryRepository mockProductCategoryRepository;
    @InjectMocks
    private ProductCategoryService sut;

    @Nested
    @DisplayName("ProductCategory 등록")
    class AddTest {
        @Test
        @DisplayName("성공")
        void addTest01() {
            // given
            var product = new Product();
            var category = new Category();
            given(mockProductCategoryRepository.save(any())).willReturn(any());

            // when
            sut.add(product, category);

            // then
            verify(mockProductCategoryRepository, times(1)).save(any());
        }
    }
}