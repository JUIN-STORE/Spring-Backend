package com.ecommerce.backend.controller;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.OrderProduct;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.enums.ProductStatus;
import com.ecommerce.backend.domain.request.ProductRequest;
import com.ecommerce.backend.service.ProductImageService;
import com.ecommerce.backend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityNotFoundException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductApiControllerTest {
    private static final String PRODUCT_END_POINT = "/api/products";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private ProductService mockProductService;

    @Mock
    private ProductImageService mockProductImageService;

    @InjectMocks
    private ProductApiController sut;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Nested
    @DisplayName("상품 등록")
    class RegisterTest {
        @Test
        @DisplayName("판매자 상품 등록 성공")
        public void registerTest01() throws Exception {
            // given
            var createRequest = getCreateRequest();

            var file = new MockMultipartFile(
                    "fileList",
                    "test.jpg",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new byte[0]);

            var json = objectMapper.writeValueAsString(createRequest);
            var request = new MockMultipartFile(
                    "request",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    json.getBytes());

            given(mockProductService.add(any(), any())).willReturn(1L);

            // when
            final ResultActions perform = mockMvc.perform(
                    multipart(PRODUCT_END_POINT + "/seller/register")
                            .file(request)
                            .file(file)
            );

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("존재하지 않는 카테고리")
        public void registerTest02() throws Exception {
            // given
            var createRequest = getCreateRequest();

            var file = new MockMultipartFile(
                    "fileList",
                    "test.jpg",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new byte[0]);

            var json = objectMapper.writeValueAsString(createRequest);
            var request = new MockMultipartFile(
                    "request",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    json.getBytes());

            given(mockProductService.add(any(), any())).willThrow(new EntityNotFoundException());

            // whenㅔ
            final ResultActions perform = mockMvc.perform(
                    multipart(PRODUCT_END_POINT + "/seller/register")
                            .file(request)
                            .file(file)
            );

            // then
            perform.andExpect(status().isOk());
            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
        }

        @Test
        @DisplayName("파일 등록 실패")
        public void registerTest03() throws Exception {
            // given
            var createRequest = getCreateRequest();

            var file = new MockMultipartFile(
                    "fileList",
                    "test.jpg",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new byte[0]);

            var json = objectMapper.writeValueAsString(createRequest);
            var request = new MockMultipartFile(
                    "request",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    json.getBytes());

            given(mockProductService.add(any(), any()))
                    .willThrow(new FileNotFoundException());

            // when
            final ResultActions perform = mockMvc.perform(
                    multipart(PRODUCT_END_POINT + "/seller/register")
                            .file(request)
                            .file(file)
            );

            // then
            perform.andExpect(status().isOk());
            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":500"));
        }

    }
    @Nested
    @DisplayName("판매자 상품 읽기")
    class AdminReadTest {
        @Test
        @DisplayName("판매자 상품 읽기 성공")
        public void adminReadTest01() throws Exception {
            // given
            var product = getProductByInfo(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
            given(mockProductService.readByProductId(1L)).willReturn(product);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(PRODUCT_END_POINT + "/seller/{productId}", 1L)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("존재하지 않는 상품")
        public void adminReadTest02() throws Exception {
            // given
            given(mockProductService.readByProductId(1L)).willThrow(new EntityNotFoundException());

            // when
            final ResultActions perform = mockMvc.perform(
                    get(PRODUCT_END_POINT + "/seller/{productId}", 1L)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
        }
    }

    @Nested
    @DisplayName("판매자 상품 삭제")
    class AdminRemoveTest {
        @Test
        @DisplayName("판매자 상품 삭제 성공")
        public void adminRemoveTest01() throws Exception {
            // given
            given(mockProductService.remove(1L)).willReturn(anyLong());

            // when
            final ResultActions perform = mockMvc.perform(
                    delete(PRODUCT_END_POINT + "/seller/{productId}", 1L)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("존재하지 않는 상품 삭제 시도로 인한 삭제 실패")
        public void adminRemoveTest02() throws Exception {
            // given
            given(mockProductService.remove(1L)).willThrow(new EntityNotFoundException());

            // when
            final ResultActions perform = mockMvc.perform(
                    delete(PRODUCT_END_POINT + "/seller/{productId}", 1L)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
        }
    }

    @Nested
    @DisplayName("상품 읽기")
    class RetrieveOneTest {
        @Test
        @DisplayName("상품 읽기 성공")
        public void RetrieveOneTest01() throws Exception {
            // given
            var product = getProductByInfo(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
            given(mockProductService.readByProductId(1L)).willReturn(product);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(PRODUCT_END_POINT + "/{productId}", 1L)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("존재하지 않는 상품 조회 시도로 인한 조회 실패")
        public void RetrieveOneTest02() throws Exception {
            given(mockProductService.readByProductId(1L)).willThrow(new EntityNotFoundException());

            // when
            final ResultActions perform = mockMvc.perform(
                    get(PRODUCT_END_POINT + "/{productId}", 1L)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
        }
    }

    @Nested
    @DisplayName("상품 목록 읽기")
    class RetrieveAllTest {
        @Test
        @DisplayName("전체 상품 목록 읽기 성공")
        public void retrieveOneTest01() throws Exception {
            // given
            var page = 0;
            var size = 10;
            var productList = getProductList();

            var pageRequest = PageRequest.of(page, size);
            var productPage = new PageImpl<>(productList, pageRequest, productList.size());

            given(mockProductService.read(pageRequest, null)).willReturn(productPage);
            given(mockProductImageService.readAllByProductId(anyList()))
                    .willReturn(productList.get(0).getProductImageList());

            // when
            final ResultActions perform = mockMvc.perform(
                    get(PRODUCT_END_POINT)
                            .param("page", Integer.toString(page))
                            .param("size", Integer.toString(size))
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("카테고리 별 상품 읽기 성공")
        public void retrieveOneTest02() throws Exception {
            // given
            var page = 0;
            var size = 10;
            var productList = getProductList();

            var pageRequest = PageRequest.of(page, size);
            var productPage = new PageImpl<>(productList, pageRequest, productList.size());

            given(mockProductService.read(pageRequest, 1L)).willReturn(productPage);
            given(mockProductImageService.readAllByProductId(anyList()))
                    .willReturn(productList.get(0).getProductImageList());

            // when
            final ResultActions perform = mockMvc.perform(
                    get(PRODUCT_END_POINT)
                            .param("page", Integer.toString(page))
                            .param("size", Integer.toString(size))
                            .param("categoryId", "1")
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }
    }


    @Nested
    @DisplayName("전체 상품 개수 읽기")
    class ReadCountTest {
        @Test
        @DisplayName("성공")
        public void readCountTest01() throws Exception {
            // given
            var count = 1L;
            given(mockProductService.readCount()).willReturn(count);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(PRODUCT_END_POINT + "/count"));

            // then
            perform.andExpect(status().isOk());
            assertTrue(
                    perform.andReturn()
                            .getResponse()
                            .getContentAsString()
                            .contentEquals(Long.toString(count)
                            )
            );
        }
    }

    @Nested
    @DisplayName("검색한 상품 개수 읽기")
    class SearchCountTest {
        @Test
        @DisplayName("성공")
        public void searchCountTest01() throws Exception {
            // given
            var count = 1L;
            var searchTitle = "title";
            given(mockProductService.readSearchCount(searchTitle)).willReturn(count);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(PRODUCT_END_POINT + "/search/count")
                            .param("productName", searchTitle));

            // then
            perform.andExpect(status().isOk());
            assertTrue(
                    perform.andReturn()
                            .getResponse()
                            .getContentAsString()
                            .contentEquals(Long.toString(count)
                            )
            );
        }
    }

    private List<Product> getProductList() {
        List<ProductImage> productImageList = new ArrayList<>();
        ProductImage.ProductImageBuilder productImageBuilder = ProductImage.builder()
                .id(1L)
                .imageName("imageName")
                .originImageName("originImageName")
                .imageUrl("imageUrl")
                .thumbnail(true);

        List<OrderProduct> orderProductList = new ArrayList<>();

        List<Product> productList = new ArrayList<>();
        Product product = getProductByInfo(productImageList, orderProductList);
        productList.add(product);

        ProductImage productImage = productImageBuilder
                .product(product)
                .build();
        productImageList.add(productImage);

        return productList;
    }

    private static Product getProductByInfo(List<ProductImage> productImageList,
                                            List<OrderProduct> orderProductList) {
        Product product = new Product(
                1L,
                "product",
                10000,
                1,
                0,
                "description",
                ProductStatus.SELL,
                new Category(),
                productImageList,
                orderProductList);
        return product;
    }

    private static ProductRequest.Create getCreateRequest() {
        return new ProductRequest.Create()
                .setCategoryId(1L)
                .setProductName("product")
                .setPrice(10000)
                .setQuantity(1)
                .setDescription("description");
    }
}