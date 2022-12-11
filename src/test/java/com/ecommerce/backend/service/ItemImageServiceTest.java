package com.ecommerce.backend.service;

import com.ecommerce.backend.FileUploadComponent;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.enums.ItemStatus;
import com.ecommerce.backend.domain.request.ItemImageRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ItemImageRepository;
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
class ItemImageServiceTest {
    @InjectMocks
    ItemImageService sut;

    @Mock
    ItemImageRepository itemImageRepository;

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
                    makeCreateRequest("name_1", "imageAbsUrl_1", "originName_1", true);
            var mockMultipartFile = makeMockMultipartFile("catList", originFileName);
            var item = makeItem();

            // when
            sut.add(request, mockMultipartFile, item);

            // then
            verify(itemImageRepository, times(1)).save(any(ItemImage.class));

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
            var itemImage1 =
                    makeItemImage(1L, "cat.jpg", "cat.jpg", "/cat.jpg", true);

            var expected = List.of(itemImage1);

            given(itemImageRepository.findByThumbnail(anyBoolean())).willReturn(Optional.of(expected));

            // when
            final List<ItemImage> actual = sut.readAllByThumbnail(false);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("썸네일이 아닌 데이터를 불러온다.")
        void readAllByThumbnailTest02() {
            // given
            var itemImage1 =
                    makeItemImage(2L, "dog.jpg", "dog.jpg", "/dog.jpg", false);
            var itemImage2 =
                    makeItemImage(3L, "dove.jpg", "dove.jpg", "/dove.jpg", false);

            var expected = List.of(itemImage1, itemImage2);

            given(itemImageRepository.findByThumbnail(anyBoolean())).willReturn(Optional.of(expected));

            // when
            final List<ItemImage> actual = sut.readAllByThumbnail(true);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("썸네일인 데이터가 없다.")
        void readAllByThumbnailTest03() {
            // given
            given(itemImageRepository.findByThumbnail(anyBoolean())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readAllByThumbnail(true));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ITEM_THUMBNAIL_NOT_FOUND);
        }


        @Test
        @DisplayName("썸네일이 아닌 데이터가 없다.")
        void readAllByThumbnailTest04() {
            // given
            given(itemImageRepository.findByThumbnail(anyBoolean())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readAllByThumbnail(false));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ITEM_THUMBNAIL_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readAllByItemId 테스트")
    class ReadAllByItemIdTest {
        @Test
        @DisplayName("데이터가 있다.")
        void readAllByItemIdTest01() {
            // given
            var itemImage1 =
                    makeItemImage(1L, "cat.jpg", "cat.jpg", "/cat.jpg", true);
            var itemImage2 =
                    makeItemImage(2L, "dog.jpg", "dog.jpg", "/dog.jpg", false);
            var itemImage3 =
                    makeItemImage(3L, "dove.jpg", "dove.jpg", "/dove.jpg", false);

            var expected = List.of(itemImage1, itemImage2, itemImage3);

            given(itemImageRepository.findAllByItemIdIn(anyList())).willReturn(Optional.of(expected));

            // when
            final List<ItemImage> actual = sut.readAllByItemId(List.of(1L, 2L, 3L));

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("데이터가 없다.")
        void readAllByItemIdTest02() {
            // given
            var expected = new ArrayList<>();

            given(itemImageRepository.findAllByItemIdIn(anyList())).willReturn(Optional.empty());

            // when
            final List<ItemImage> actual = sut.readAllByItemId(List.of(1L, 2L, 3L));

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



    private Item makeItem() {
        return Item.builder()
                .id(1L)
                .name("상품1")
                .price(1000)
                .quantity(100)
                .soldCount(2)
                .description("상품1 설명")
                .itemStatus(ItemStatus.SOLD_OUT)
                .category(null)
                .build();
    }

    private ItemImageRequest.Create makeCreateRequest(String name
            , String originName
            , String imageUrl
            , Boolean isThumbnail) {

        var request = new ItemImageRequest.Create(isThumbnail);

        return request
                .setImageName(name)
                .setOriginImageName(originName)
                .setImageUrl(imageUrl);
    }


    private ItemImage makeItemImage(Long id
            , String name
            , String originName
            , String imageUrl
            , Boolean isThumbnail) {

        return ItemImage.builder()
                .id(id)
                .name(name)
                .originName(originName)
                .imageUrl(imageUrl)
                .thumbnail(isThumbnail)
                .build();
    }
}