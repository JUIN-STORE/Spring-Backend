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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    @DisplayName("상품 등록 테스트")
    class AddTest {
        @Test
        @DisplayName("성공")
        void addTest01() throws IOException {
            // given
            var multipartFileList = new ArrayList<MultipartFile>();

            var thumbnailFile = new MockMultipartFile("name", new byte[0]);
            multipartFileList.add(new MockMultipartFile("name", new byte[0]));

            var category = new Category();
            var request = new ProductRequest.Create()
                    .setCategoryId(1L)
                    .setDescription("description")
                    .setPrice(10000)
                    .setProductName("productName")
                    .setQuantity(1);

            var product = getProduct();

            given(categoryService.readById(anyLong())).willReturn(category);
            willDoNothing().given(productCategoryService).add(any(), any());
            given(productRepository.save(any())).willReturn(product);
            willDoNothing().given(productImageService).saveProductImage(any(), any(), any());

            // when
            sut.add(request,thumbnailFile, multipartFileList);

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

            var request = new ProductRequest.Create()
                    .setCategoryId(1L)
                    .setDescription("description")
                    .setPrice(10000)
                    .setProductName("productName")
                    .setQuantity(1);

            given(categoryService.readById(1L)).willThrow(new EntityNotFoundException(Msg.CATEGORY_NOT_FOUND));

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.add(request, thumbnailFile, multipartFileList));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.CATEGORY_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("상품 조회 테스트")
    class ReadByProductIdTest {
        @Test
        @DisplayName("성공")
        public void readByProductIdTest01() {
            // given
            var productId = 1L;
            var product = getProduct();

            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

            // when
            Product actual = sut.readByProductId(productId);

            // then
            assertEquals(product, actual);
        }

        @Test
        @DisplayName("존재하지 않는 상품")
        public void readByProductIdTest02() {
            // given
            var productId = 1L;
            given(productRepository.findById(anyLong())).willThrow(new EntityNotFoundException(Msg.PRODUCT_NOT_FOUND));

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.readByProductId(productId));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.PRODUCT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("상품 아이디 리스트로 상품 리스트 조회")
    class ReadByIdListTest {
        @Test
        @DisplayName("성공")
        public void readByIdListTest01() {
            // given
            var productId = 1L;
            var productIdList = new ArrayList<Long>();
            productIdList.add(productId);

            var product = getProduct();

            var productList = new ArrayList<Product>();
            productList.add(product);

            given(productRepository.findByIdIn(productIdList)).willReturn(Optional.of(productList));

            // when
            List<Product> actual = sut.readByIdList(productIdList);

            // then
            assertEquals(productList, actual);
        }
    }

    @Nested
    @DisplayName("상품 삭제 테스트")
    class RemoveTest {
        @Test
        @DisplayName("성공")
        public void removeTest01() {
            // given
            var productId = 1L;
            var product = getProduct();
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            Long actual = sut.remove(productId);

            // then
            assertEquals(productId, actual);
            verify(productRepository, times(1)).findById(anyLong());
            assertEquals(product.getProductStatus(), ProductStatus.SOLD_OUT);
        }

        @Test
        @DisplayName("존재하지 않는 상품")
        public void removeTest02() {
            // given
            var productId = 1L;
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.remove(productId));

            // then
            actual.isInstanceOf(EntityNotFoundException.class)
                    .hasMessage(Msg.PRODUCT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("상품 목록 조회")
    class ReadTest {
        @Test
        @DisplayName("전체 읽기 성공")
        public void readTest01() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var productList = new ArrayList<Product>();
            var product = getProduct();
            productList.add(product);

            var productPage = new PageImpl<>(productList, pageRequest, productList.size());

            given(productRepository.findAll(pageRequest)).willReturn(productPage);

            // when
            Page<Product> actual = sut.read(pageRequest, null);

            // then
            assertEquals(productPage, actual);
        }

        @Test
        @DisplayName("카테고리 내 상품 읽기 성공")
        public void readTest02() {
            // given
            var categoryId = 1L;
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var productList = new ArrayList<Product>();
            var product = getProduct();
            productList.add(product);

            var productPage = new PageImpl<>(productList, pageRequest, productList.size());

            given(productRepository.findByCategoryId(pageRequest, categoryId)).willReturn(productPage);

            // when
            Page<Product> actual = sut.read(pageRequest, categoryId);

            // then
            assertEquals(productPage, actual);
        }
    }

    @Nested
    @DisplayName("상품 목록 개수 세기")
    class ReadCountTest {
        @Test
        @DisplayName("성공")
        public void readCountTest() {
            // given
            var count = 10L;
            given(productRepository.count()).willReturn(count);

            // when
            Long actual = sut.readCount();

            // then
            assertEquals(count, actual);
        }
    }

    @Nested
    @DisplayName("검색")
    class SearchTest {
        @Test
        @DisplayName("검색어로 검색하기 성공")
        public void searchTest01() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);
            var productName = "productName";

            var productList = new ArrayList<Product>();
            var product = getProduct();
            productList.add(product);

            var productPage = new PageImpl<>(productList, pageRequest, productList.size());
            given(productRepository.findByProductNameContaining(pageRequest, productName)).willReturn(productPage);

            // when
            Page<Product> actual = sut.search(pageRequest, productName, null);

            // then
            assertEquals(productPage, actual);
        }


        @Test
        @DisplayName("카테고리 아이디로 검색하기 성공")
        public void searchTest02() {
            // given
            var page = 0;
            var size = 10;
            var categoryId = 1L;
            var pageRequest = PageRequest.of(page, size);
            var productName = "productName";

            var productList = new ArrayList<Product>();
            var product = getProduct();
            productList.add(product);

            var productPage = new PageImpl<>(productList, pageRequest, productList.size());
            given(productRepository.findByProductNameContainingAndCategoryId(pageRequest, productName, categoryId)).willReturn(productPage);

            // when
            Page<Product> actual = sut.search(pageRequest, productName, categoryId);

            // then
            assertEquals(productPage, actual);
        }
    }

    @Nested
    @DisplayName("검색 상품 개수 세기")
    class ReadSearchCountTest {
        @Test
        @DisplayName("카테고리 아이디로 검색 상품 개수 세기")
        public void readSearchCountTest01() {
            // given
            var count = 1L;
            var searchTitle = "searchTitle";
            given(productRepository.countByProductNameContaining(searchTitle)).willReturn(count);

            // when
            Long actual = sut.readSearchCount(searchTitle);

            // then
            assertEquals(count, actual);
        }
    }

    private static Product getProduct() {
        return Product.builder()
                .id(1L)
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