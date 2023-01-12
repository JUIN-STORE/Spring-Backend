package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ItemRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class ItemImageQueryServiceTest {
    @InjectMocks
    private ItemImageQueryService sut;

    @Mock private ItemRepository itemRepository;

    @Mock private ItemImageQueryService itemImageQueryService;

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

            given(itemImageQueryService.readAllByThumbnail(anyBoolean())).willReturn(expected);

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

            given(itemImageQueryService.readAllByThumbnail(anyBoolean())).willReturn(expected);

            // when
            final List<ItemImage> actual = sut.readAllByThumbnail(true);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("썸네일인 데이터가 없다.")
        void readAllByThumbnailTest03() {
            // given
//            given(itemImageRepository.findByThumbnail(anyBoolean())).willReturn(Optional.empty());

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
//            given(itemImageRepository.findByThumbnail(anyBoolean())).willReturn(Optional.empty());

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

            given(itemImageQueryService.readAllByThumbnail(anyBoolean())).willReturn(expected);

            // when
            final List<ItemImage> actual = sut.readAllByItemIdIn(List.of(1L, 2L, 3L));

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("데이터가 없다.")
        void readAllByItemIdTest02() {
            // given
            var expected = new ArrayList<>();
            given(itemImageQueryService.readAllByItemIdIn(anyList())).willReturn(null);

            // when
            final List<ItemImage> actual = sut.readAllByItemIdIn(List.of(1L, 2L, 3L));

            // then
            assertIterableEquals(expected, actual);
        }
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