package com.ecommerce.backend.service;

import com.ecommerce.backend.FileUploadComponent;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.enums.ProductStatus;
import com.ecommerce.backend.domain.request.ProductImageRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ProductImageRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class ProductImageServiceTest {
    @InjectMocks
    ProductImageService sut;

    @Mock
    ProductImageRepository productImageRepository;

    @Mock
    FileUploadComponent fileUploadComponent;

    @Nested
    @DisplayName("add 테스트")
    class AddTest {
        @Test
        @DisplayName("업로드 성공")
        void addTest01() throws IOException {
            // given
            var originFileName = "cat.jpg";
            var request =
                    makeCreateRequest("imageName_1", "imageAbsUrl_1", "originImageName_1", true);
            var mockMultipartFile = makeMockMultipartFile("catList", originFileName);
            var product = makeProduct();

            // when
            sut.add(request, mockMultipartFile, product);

            // then
            verify(productImageRepository, times(1)).save(any(ProductImage.class));

        }

        // FIXME: 실패 케이스...?
    }

    @Nested
    @DisplayName("readAllByThumbnail 테스트")
    class ReadAllByThumbnail {
        @Test
        @DisplayName("썸네일 데이터를 불러온다.")
        void readAllByThumbnailTest01() {
            // given
            var productImage1 =
                    makeProductImage(1L, "cat.jpg", "cat.jpg", "/cat.jpg", true);

            var expected = List.of(productImage1);

            given(productImageRepository.findByThumbnail(anyBoolean())).willReturn(Optional.of(expected));

            // when
            final List<ProductImage> actual = sut.readAllByThumbnail(false);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("썸네일이 아닌 데이터를 불러온다.")
        void readAllByThumbnailTest02() {
            // given
            var productImage1 =
                    makeProductImage(2L, "dog.jpg", "dog.jpg", "/dog.jpg", false);
            var productImage2 =
                    makeProductImage(3L, "dove.jpg", "dove.jpg", "/dove.jpg", false);

            var expected = List.of(productImage1, productImage2);

            given(productImageRepository.findByThumbnail(anyBoolean())).willReturn(Optional.of(expected));

            // when
            final List<ProductImage> actual = sut.readAllByThumbnail(true);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("썸네일인 데이터가 없다.")
        void readAllByThumbnailTest03() {
            // given
            given(productImageRepository.findByThumbnail(anyBoolean())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readAllByThumbnail(true));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.PRODUCT_THUMBNAIL_NOT_FOUND);
        }


        @Test
        @DisplayName("썸네일이 아닌 데이터가 없다.")
        void readAllByThumbnailTest04() {
            // given
            given(productImageRepository.findByThumbnail(anyBoolean())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readAllByThumbnail(false));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.PRODUCT_THUMBNAIL_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readAllByProductId 테스트")
    class ReadAllByProductIdTest {
        @Test
        @DisplayName("데이터가 있다.")
        void readAllByProductIdTest01() {
            // given
            var productImage1 =
                    makeProductImage(1L, "cat.jpg", "cat.jpg", "/cat.jpg", true);
            var productImage2 =
                    makeProductImage(2L, "dog.jpg", "dog.jpg", "/dog.jpg", false);
            var productImage3 =
                    makeProductImage(3L, "dove.jpg", "dove.jpg", "/dove.jpg", false);

            var expected = List.of(productImage1, productImage2, productImage3);

            given(productImageRepository.findAllByProductIdIn(anyList())).willReturn(Optional.of(expected));

            // when
            final List<ProductImage> actual = sut.readAllByProductId(List.of(1L, 2L, 3L));

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("데이터가 없다.")
        void readAllByProductIdTest02() {
            // given
            var expected = new ArrayList<>();

            given(productImageRepository.findAllByProductIdIn(anyList())).willReturn(Optional.empty());

            // when
            final List<ProductImage> actual = sut.readAllByProductId(List.of(1L, 2L, 3L));

            // then
            assertIterableEquals(expected, actual);
        }
    }

    private MockMultipartFile makeMockMultipartFile(String name, String originalFilename) {

        return new MockMultipartFile(
                "catList",
                "cat.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new byte[0]);
    }



    private Product makeProduct() {
        return Product.builder()
                .id(1L)
                .productName("상품1")
                .price(1000)
                .quantity(100)
                .soldCount(2)
                .description("상품1 설명")
                .productStatus(ProductStatus.SOLD_OUT)
                .category(null)
                .build();
    }

    private ProductImageRequest.Create makeCreateRequest(String imageName
            , String originImageName
            , String imageUrl
            , Boolean isThumbnail) {

        var request = new ProductImageRequest.Create(isThumbnail);

        return request
                .setImageName(imageName)
                .setOriginImageName(originImageName)
                .setImageUrl(imageUrl);
    }


    private ProductImage makeProductImage(Long id
            , String imageName
            , String originImageName
            , String imageUrl
            , Boolean isThumbnail) {

        return ProductImage.builder()
                .id(id)
                .imageName(imageName)
                .originImageName(originImageName)
                .imageUrl(imageUrl)
                .thumbnail(isThumbnail)
                .build();
    }
}