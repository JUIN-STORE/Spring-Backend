package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.enums.ProductStatus;
import com.ecommerce.backend.domain.request.ProductRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ProductRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService sut;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageService productImageService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductCategoryService productCategoryService;

    @Nested
    @DisplayName("add 테스트")
    class AddTest {
        @Test
        @DisplayName("상품 추가 성공")
        void addTest01() throws IOException {
            // given
            var multipartFileList = new ArrayList<MultipartFile>();

            var thumbnailFile = new MockMultipartFile("name", new byte[0]);
            multipartFileList.add(new MockMultipartFile("name", new byte[0]));

            var category = new Category();
            var request = makeProductRequest(1L);

            var product = makeProduct(1L);

            given(categoryService.readById(anyLong())).willReturn(category);
            willDoNothing().given(productCategoryService).add(any(), any());
            given(productRepository.save(any())).willReturn(product);
            willDoNothing().given(productImageService).add(any(), any(), any());

            // when
            sut.add(request, thumbnailFile, multipartFileList);

            // then
            verify(productRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 카테고리")
        void addTest02() {
            // given
            var thumbnailFile = new MockMultipartFile("name", new byte[0]);
            var multipartFileList = new ArrayList<MultipartFile>();
            multipartFileList.add(new MockMultipartFile("name", new byte[0]));

            var request = makeProductRequest(2L);

            given(categoryService.readById(anyLong())).willThrow(new EntityNotFoundException(Msg.CATEGORY_NOT_FOUND));

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.add(request, thumbnailFile, multipartFileList));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.CATEGORY_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readById 테스트")
    class ReadByIdTest {
        @Test
        @DisplayName("productId로 읽기 성공")
        void readByIdTest01() {
            // given
            var productId = 1L;
            var product = makeProduct(productId);

            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

            // when
            Product actual = sut.readById(productId);

            // then
            assertEquals(product, actual);
        }

        @Test
        @DisplayName("존재하지 않는 상품")
        void readByIdTest02() {
            // given
            var productId = 1L;
            given(productRepository.findById(anyLong())).willThrow(new EntityNotFoundException(Msg.PRODUCT_NOT_FOUND));

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readById(productId));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.PRODUCT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readAllByIdList 테스트")
    class ReadAllByIdListTest {
        @Test
        @DisplayName("상품 리스트가 하나 이상일 때")
        void ReadAllByIdListTest01() {
            // given
            var productIdList = List.of(1L, 2L);
            var productList = List.of(makeProduct(1L), makeProduct(2L));

            given(productRepository.findAllByIdIn(productIdList)).willReturn(Optional.of(productList));

            // when
            final List<Product> actual = sut.readAllByIdList(productIdList);

            // then
            assertEquals(productList, actual);
        }

        @Test
        @DisplayName("상품 리스트가 하나도 없을 때")
        void ReadAllByIdListTest02() {
            // given
            var productIdList = List.of(1L, 2L);

            given(productRepository.findAllByIdIn(anyList())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readAllByIdList(productIdList));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.PRODUCT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readAll 테스트")
    class ReadAllTest {
        @Test
        @DisplayName("등록된 모든 상품 읽기 성공")
        void readAllTest01() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var productId = 33L;
            var productList = List.of(makeProduct(productId));

            var productPage = new PageImpl<>(productList, pageRequest, productList.size());

            given(productRepository.findAll(pageRequest)).willReturn(productPage);

            // when
            final Page<Product> actual = sut.readAll(pageRequest);

            // then
            assertEquals(productPage, actual);
        }

        @Test
        @DisplayName("상품이 하나도 없을 때")
        void readAllTest02() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var expected = makeEmptyPageProduct();
            given(productRepository.findAll(pageRequest)).willReturn(expected);

            // when
            final Page<Product> actual = sut.readAll(pageRequest);

            // then
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("readAllByCategoryId 테스트")
    class ReadAllByCategoryIdTest{
        @Test
        @DisplayName("카테고리 내에서 전체 상품 읽기 성공")
        void readAllByCategoryIdTest01() {
            // given
            var categoryId = 1L;
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var productId = 342L;
            var productList = List.of(makeProduct(productId));

            var productPage = new PageImpl<>(productList, pageRequest, productList.size());

            given(productRepository.findAllByCategoryId(pageRequest, categoryId)).willReturn(productPage);

            // when
            final Page<Product> actual = sut.readAllByCategoryId(pageRequest, categoryId);

            // then
            assertEquals(productPage, actual);
        }

        @Test
        @DisplayName("카테고리 내에 상품이 하나도 없을 때")
        void readAllByCategoryIdTest02() {
            // given
            var categoryId = 9L;
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var expected = makeEmptyPageProduct();
            given(productRepository.findAllByCategoryId(pageRequest, categoryId)).willReturn(expected);

            // when
            final Page<Product> actual = sut.readAllByCategoryId(pageRequest, categoryId);

            // then
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("readAllByProductNameContaining 테스트")
    class ReadAllByProductNameContainingTest {
        @Test
        @DisplayName("카테고리와 무관하게 검색어로 검색 성공")
        void readAllByProductNameContainingTest01() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);
            var productName = "productName01";
            var productId = 912L;

            var productList = List.of(makeProduct(productId));
            var productPage = new PageImpl<>(productList, pageRequest, productList.size());

            given(productRepository.findAllByProductNameContaining(pageRequest, productName)).willReturn(productPage);

            // when
            final Page<Product> actual = sut.readAllByProductNameContaining(pageRequest, productName);

            // then
            assertEquals(productPage, actual);
        }

        @Test
        @DisplayName("카테고리와 무관하게 검색 실패")
        void readAllByProductNameContainingTest02() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);
            var productName = "productName01";

            var expected = makeEmptyPageProduct();

            given(productRepository.findAllByProductNameContaining(pageRequest, productName)).willReturn(expected);

            // when
            final Page<Product> actual = sut.readAllByProductNameContaining(pageRequest, productName);

            // then
            assertEquals(expected, actual);
        }
    }


    @Nested
    @DisplayName("readAllByProductNameContainingAndCategoryId 테스트")
    class ReadAllByProductNameContainingAndCategoryId {
        @Test
        @DisplayName("카테고리 안에서 검색 성공")
        void readAllByProductNameContainingTest01() {
            // given
            var page = 0;
            var size = 10;
            var categoryId = 2L;
            var pageRequest = PageRequest.of(page, size);
            var productName = "productName07";

            var productList = List.of(makeProduct(33L), makeProduct(44L));

            var productPage = new PageImpl<>(productList, pageRequest, productList.size());
            given(productRepository.findAllByProductNameContainingAndCategoryId(pageRequest, productName, categoryId))
                    .willReturn(productPage);

            // when
            final Page<Product> actual =
                    sut.readAllByProductNameContainingAndCategoryId(pageRequest, productName, categoryId);

            // then
            assertEquals(productPage, actual);
        }

        @Test
        @DisplayName("카테고리 안에서 검색 실패")
        void readAllByProductNameContainingTest02() {
            // given
            var page = 0;
            var size = 10;
            var categoryId = 2L;
            var pageRequest = PageRequest.of(page, size);
            var productName = "productName9999";

            var expected = makeEmptyPageProduct();

            given(productRepository.findAllByProductNameContainingAndCategoryId(pageRequest, productName, categoryId))
                    .willReturn(expected);

            // when
            final Page<Product> actual =
                    sut.readAllByProductNameContainingAndCategoryId(pageRequest, productName, categoryId);

            // then
            assertEquals(expected, actual);
        }
    }


    @Nested
    @DisplayName("total 테스트")
    class TotalTest {
        @Test
        @DisplayName("전체 상품 카운팅하기")
        void totalTest01() {
            // given
            var count = 10L;
            given(productRepository.count()).willReturn(count);

            // when
            final Long actual = sut.total();

            // then
            assertEquals(count, actual);
        }
    }


    @Nested
    @DisplayName("totalByProductNameContaining 테스트")
    class TotalByProductNameContainingTest {
        @Test
        @DisplayName("카테고리 아이디로 검색한 상품의 개수 세기")
        void readSearchCountTest01() {
            // given
            var count = 1L;
            var searchTitle = "searchTitle";
            given(productRepository.countByProductNameContaining(searchTitle)).willReturn(count);

            // when
            final Long actual = sut.totalByProductNameContaining(searchTitle);

            // then
            assertEquals(count, actual);
        }
    }


    @Nested
    @DisplayName("remove Test")
    class RemoveTest {
        @Test
        @DisplayName("상품 삭제 성공")
        void removeTest01() {
            // given
            var productId = 11L;
            var product = makeProduct(productId);
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            final Long actual = sut.remove(productId);

            // then
            assertEquals(ProductStatus.SOLD_OUT, product.getProductStatus());
        }

        @Test
        @DisplayName("존재하지 않는 상품 삭제 실패")
        void removeTest02() {
            // given
            var productId = 1L;
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.remove(productId));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.PRODUCT_NOT_FOUND);
        }
    }

    private Page<Product> makeEmptyPageProduct() {
        return new PageImpl<>(Collections.emptyList());
    }

    private ProductRequest.Create makeProductRequest(Long categoryId) {
        var request = new ProductRequest.Create();

        return request
                .setCategoryId(categoryId)
                .setDescription("description")
                .setPrice(10000)
                .setProductName("productName")
                .setQuantity(1);
    }

    private Product makeProduct(Long productId) {
        return Product.builder()
                .id(productId)
                .productName("name")
                .price(10000)
                .quantity(1)
                .soldCount(1)
                .description("description")
                .productStatus(ProductStatus.READY)
                .category(new Category())
                .build();
    }
}