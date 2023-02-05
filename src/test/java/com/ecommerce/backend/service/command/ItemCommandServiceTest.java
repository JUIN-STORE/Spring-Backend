package com.ecommerce.backend.service.command;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.enums.ItemStatus;
import com.ecommerce.backend.domain.request.ItemRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ItemRepository;
import com.ecommerce.backend.service.query.CategoryQueryService;
import com.ecommerce.backend.service.query.ItemQueryService;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class ItemCommandServiceTest {
    @InjectMocks
    private ItemCommandService sut;

    @Mock private ItemRepository itemRepository;

    @Mock private ItemQueryService itemQueryService;
    @Mock private CategoryQueryService categoryQueryService;

    @Mock private ItemImageCommandService itemImageCommandService;
    @Mock private ItemCategoryCommandService itemCategoryCommandService;

    @Nested
    @DisplayName("add 테스트")
    class AddTest {
//        @Test
//        @DisplayName("상품 추가 성공")
//        void addTest01() throws IOException {
//            // given
//            var multipartFileList = new ArrayList<MultipartFile>();
//
//            var thumbnailFile = new MockMultipartFile("name", new byte[0]);
//            multipartFileList.add(new MockMultipartFile("name", new byte[0]));
//
//            var category = makeCategory();
//            var request = makeItemRequest(1L);
//
//            var item = makeItem(1L);
//
//            given(categoryQueryService.readById(anyLong())).willReturn(category);
//            willDoNothing().given(itemCategoryCommandService).add(any(), any());
//            given(itemRepository.save(any())).willReturn(item);
//            willDoNothing().given(itemImageCommandService).addOriginalImage(any(), any(), any());
//
//            // when
//            sut.add(request, thumbnailFile, multipartFileList);
//
//            // then
//            verify(itemRepository, times(1)).save(any());
//        }

        @Test
        @DisplayName("존재하지 않는 카테고리")
        void addTest02() {
            // given
            var thumbnailFile = new MockMultipartFile("name", new byte[0]);
            var multipartFileList = new ArrayList<MultipartFile>();
            multipartFileList.add(new MockMultipartFile("name", new byte[0]));

            var request = makeItemRequest(2L);

            given(categoryQueryService.readById(anyLong())).willThrow(new EntityNotFoundException(Msg.CATEGORY_NOT_FOUND));

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.add(request, thumbnailFile, multipartFileList));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.CATEGORY_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("remove 테스트")
    class RemoveTest {
        @Test
        @DisplayName("상품 삭제 성공")
        void removeTest01() {
            // given
            var itemId = 11L;
            var item = makeItem(itemId);
            given(itemQueryService.readById(itemId)).willReturn(item);

            // when
            final Long actual = sut.remove(itemId);

            // then
            assertEquals(ItemStatus.SOLD_OUT, item.getItemStatus());
        }

        @Test
        @DisplayName("존재하지 않는 상품 삭제 실패")
        void removeTest02() {
            // given
            var itemId = 1L;
            willThrow(new EntityNotFoundException(Msg.ITEM_NOT_FOUND)).given(itemQueryService).readById(itemId);

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.remove(itemId));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ITEM_NOT_FOUND);
        }
    }

    private ItemRequest.Create makeItemRequest(Long categoryId) {
        var request = new ItemRequest.Create();

        return request
                .setCategoryId(categoryId)
                .setDescription("description")
                .setPrice(10000)
                .setName("name")
                .setQuantity(1);
    }

    private Category makeCategory() {
        return Category.builder().build();
    }


    private Item makeItem(Long itemId) {
        return Item.builder()
                .id(itemId)
                .name("name")
                .price(10000)
                .quantity(1)
                .soldCount(1)
                .description("description")
                .itemStatus(ItemStatus.READY)
                .category(makeCategory())
                .build();
    }
}