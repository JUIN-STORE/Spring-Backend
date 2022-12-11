package com.ecommerce.backend.relation;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.enums.ProductStatus;
import com.ecommerce.backend.domain.response.ProductResponse;
import com.ecommerce.backend.service.ProductImageService;
import com.ecommerce.backend.service.ProductService;
import com.ecommerce.backend.service.relation.ProductRelationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class ProductRelationServiceTest {
    @InjectMocks
    ProductRelationService sut;

    @Mock
    ProductService productService;

    @Mock
    ProductImageService productImageService;

    @Nested
    @DisplayName("display 테스트")
    class DisplayTest {
        @Test
        @DisplayName("categoryId가 없을 때")
        void displayTest01() {
            // given
            var page = 0;
            var size = 10;
            var pageable = PageRequest.of(page, size);

            var product = makeProduct(1L);
            var productList = List.of(product);

            var productImageList = makeProductImageList(20L, product);

            given(productImageService.readAllByProductId(List.of(1L))).willReturn(productImageList);
            given(productService.readAll(pageable))
                    .willReturn(new PageImpl<>(productList, pageable, productList.size()));

            var expected = makeProductReadResponseList(productList, productImageList);

            // when
            final List<ProductResponse.Read> actual = sut.display(pageable, null);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("categoryId가 있을 때")
        void displayTest02() {
            // given
            var page = 0;
            var size = 10;
            var pageable = PageRequest.of(page, size);
            var categoryId = 10L;

            var product = makeProduct(1L);
            var productList = List.of(product);

            var productImageList = makeProductImageList(20L, product);

            given(productImageService.readAllByProductId(List.of(1L))).willReturn(productImageList);
            given(productService.readAllByCategoryId(pageable, categoryId))
                    .willReturn(new PageImpl<>(productList, pageable, productList.size()));

            var expected = makeProductReadResponseList(productList, productImageList);

            // when
            final List<ProductResponse.Read> actual = sut.display(pageable, categoryId);

            // then
            assertIterableEquals(expected, actual);
        }

    }


    @Nested
    @DisplayName("search 테스트")
    class SearchTest {
        @Test
        @DisplayName("categoryId가 없을 때")
        void searchTest01() {
            // given
            var page = 0;
            var size = 10;
            var pageable = PageRequest.of(page, size);
            var searchTitle = "이게 검색어다!";

            var product = makeProduct(1L);
            var productList = List.of(product);

            var productImageList = makeProductImageList(20L, product);

            given(productImageService.readAllByProductId(List.of(1L))).willReturn(productImageList);
            given(productService.readAllByProductNameContaining(pageable, searchTitle))
                    .willReturn(new PageImpl<>(productList, pageable, productList.size()));

            var expected = makeProductReadResponseList(productList, productImageList);

            // when
            final List<ProductResponse.Read> actual = sut.search(pageable, searchTitle, null);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("categoryId가 있을 때")
        void searchTest02() {
            // given
            var page = 0;
            var size = 10;
            var pageable = PageRequest.of(page, size);
            var searchTitle = "이게 검색어다!";
            var categoryId = 10L;

            var product = makeProduct(1L);
            var productList = List.of(product);

            var productImageList = makeProductImageList(20L, product);

            given(productImageService.readAllByProductId(List.of(1L))).willReturn(productImageList);
            given(productService.readAllByProductNameContainingAndCategoryId(pageable, searchTitle, categoryId))
                    .willReturn(new PageImpl<>(productList, pageable, productList.size()));

            var expected = makeProductReadResponseList(productList, productImageList);

            // when
            final List<ProductResponse.Read> actual = sut.search(pageable, searchTitle, categoryId);

            // then
            assertIterableEquals(expected, actual);
        }

    }

    @Nested
    @DisplayName("makeProductReadResponse 테스트")
    class MakeProductReadResponseTest {
        @Test
        @DisplayName("상품에 2개 이상의 이미지가 있을 때")
        void makeProductReadResponseTest01() throws Exception {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var productIdList = List.of(1L);
            var product = makeProduct(1L);
            var productList = List.of(product);

            var productPage = new PageImpl<>(productList, pageRequest, productList.size());

            var productImageList = makeProductImageList(20L, product);
            given(productImageService.readAllByProductId(productIdList)).willReturn(productImageList);

            var expected = makeProductReadResponseList(productList, productImageList);

            // when
            Method method = ProductRelationService.class.getDeclaredMethod("makeProductReadResponseList", Page.class);
            method.setAccessible(true);
            final List<ProductResponse.Read> actual = (List<ProductResponse.Read>) method.invoke(sut, productPage);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("상품에 1개 이미지만 있을 때")
        void makeProductReadResponseTest02() throws Exception {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var productIdList = List.of(1L);
            var product = makeProduct(1L);
            var productList = List.of(product);

            var productPage = new PageImpl<>(productList, pageRequest, productList.size());

            var productImageList = List.of(
                    makeProductImage(88L
                            , "cat.jpg"
                            , "cat.jpg"
                            , "/cat.jpg"
                            , true
                            , product
                    )
            );
            given(productImageService.readAllByProductId(productIdList)).willReturn(productImageList);

            var expected = makeProductReadResponseList(productList, productImageList);

            // when
            Method method = ProductRelationService.class.getDeclaredMethod("makeProductReadResponseList", Page.class);
            method.setAccessible(true);
            final List<ProductResponse.Read> actual = (List<ProductResponse.Read>) method.invoke(sut, productPage);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("상품에 이미지가 하나도 없을 떼")
        void makeProductReadResponseTest03() throws Exception {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var productIdList = List.of(1L);
            var product = makeProduct(1L);
            var productList = List.of(product);

            var productPage = new PageImpl<>(productList, pageRequest, productList.size());

            given(productImageService.readAllByProductId(productIdList)).willReturn(new ArrayList<>());

            var expected = makeProductReadResponseList(productList, new ArrayList<>());

            // when
            Method method = ProductRelationService.class.getDeclaredMethod("makeProductReadResponseList", Page.class);
            method.setAccessible(true);
            final List<ProductResponse.Read> actual = (List<ProductResponse.Read>) method.invoke(sut, productPage);

            // then
            assertIterableEquals(expected, actual);
        }
    }


    private List<ProductImage> makeProductImageList(Long productImageId, Product product) {
        return List.of(
                makeProductImage(productImageId
                        , "cat.jpg"
                        , "cat.jpg"
                        , "/cat.jpg"
                        , true
                        , product
                ),

                makeProductImage(productImageId + 10L
                        , "dog.jpg"
                        , "dog.jpg"
                        , "/dog.jpg"
                        , false
                        , product
                )
        );
    }

    private List<ProductResponse.Read> makeProductReadResponseList(List<Product> productList
            , List<ProductImage> productImageList) {

        return productList.stream()
                .map(image -> ProductResponse.Read.of(image, productImageList))
                .collect(Collectors.toList());
    }

    private ProductImage makeProductImage(Long id
            , String imageName
            , String originImageName
            , String imageUrl
            , Boolean isThumbnail
            , Product product) {

        return ProductImage.builder()
                .id(id)
                .imageName(imageName)
                .originImageName(originImageName)
                .imageUrl(imageUrl)
                .thumbnail(isThumbnail)
                .product(product)
                .build();
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