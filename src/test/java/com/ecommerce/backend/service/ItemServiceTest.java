package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.enums.ItemStatus;
import com.ecommerce.backend.domain.request.ItemRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.ItemRepository;
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
class ItemServiceTest {
    @InjectMocks
    private ItemService sut;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemImageService itemImageService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ItemCategoryService itemCategoryService;

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
            var request = makeItemRequest(1L);

            var item = makeItem(1L);

            given(categoryService.readById(anyLong())).willReturn(category);
            willDoNothing().given(itemCategoryService).add(any(), any());
            given(itemRepository.save(any())).willReturn(item);
            willDoNothing().given(itemImageService).add(any(), any(), any());

            // when
            sut.add(request, thumbnailFile, multipartFileList);

            // then
            verify(itemRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 카테고리")
        void addTest02() {
            // given
            var thumbnailFile = new MockMultipartFile("name", new byte[0]);
            var multipartFileList = new ArrayList<MultipartFile>();
            multipartFileList.add(new MockMultipartFile("name", new byte[0]));

            var request = makeItemRequest(2L);

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
        @DisplayName("itemId로 읽기 성공")
        void readByIdTest01() {
            // given
            var itemId = 1L;
            var item = makeItem(itemId);

            given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));

            // when
            Item actual = sut.readById(itemId);

            // then
            assertEquals(item, actual);
        }

        @Test
        @DisplayName("존재하지 않는 상품")
        void readByIdTest02() {
            // given
            var itemId = 1L;
            given(itemRepository.findById(anyLong())).willThrow(new EntityNotFoundException(Msg.PRODUCT_NOT_FOUND));

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readById(itemId));

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
            var itemIdList = List.of(1L, 2L);
            var itemList = List.of(makeItem(1L), makeItem(2L));

            given(itemRepository.findAllByIdIn(itemIdList)).willReturn(Optional.of(itemList));

            // when
            final List<Item> actual = sut.readAllByIdList(itemIdList);

            // then
            assertEquals(itemList, actual);
        }

        @Test
        @DisplayName("상품 리스트가 하나도 없을 때")
        void ReadAllByIdListTest02() {
            // given
            var itemIdList = List.of(1L, 2L);

            given(itemRepository.findAllByIdIn(anyList())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readAllByIdList(itemIdList));

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

            var itemId = 33L;
            var itemList = List.of(makeItem(itemId));

            var itemPage = new PageImpl<>(itemList, pageRequest, itemList.size());

            given(itemRepository.findAll(pageRequest)).willReturn(itemPage);

            // when
            final Page<Item> actual = sut.readAll(pageRequest);

            // then
            assertEquals(itemPage, actual);
        }

        @Test
        @DisplayName("상품이 하나도 없을 때")
        void readAllTest02() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var expected = makeEmptyPageItem();
            given(itemRepository.findAll(pageRequest)).willReturn(expected);

            // when
            final Page<Item> actual = sut.readAll(pageRequest);

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

            var itemId = 342L;
            var itemList = List.of(makeItem(itemId));

            var itemPage = new PageImpl<>(itemList, pageRequest, itemList.size());

            given(itemRepository.findAllByCategoryId(pageRequest, categoryId)).willReturn(itemPage);

            // when
            final Page<Item> actual = sut.readAllByCategoryId(pageRequest, categoryId);

            // then
            assertEquals(itemPage, actual);
        }

        @Test
        @DisplayName("카테고리 내에 상품이 하나도 없을 때")
        void readAllByCategoryIdTest02() {
            // given
            var categoryId = 9L;
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var expected = makeEmptyPageItem();
            given(itemRepository.findAllByCategoryId(pageRequest, categoryId)).willReturn(expected);

            // when
            final Page<Item> actual = sut.readAllByCategoryId(pageRequest, categoryId);

            // then
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("readAllByNameContaining 테스트")
    class ReadAllByNameContainingTest {
        @Test
        @DisplayName("카테고리와 무관하게 검색어로 검색 성공")
        void readAllByNameContainingTest01() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);
            var name = "name01";
            var itemId = 912L;

            var itemList = List.of(makeItem(itemId));
            var itemPage = new PageImpl<>(itemList, pageRequest, itemList.size());

            given(itemRepository.findAllByNameContaining(pageRequest, name)).willReturn(itemPage);

            // when
            final Page<Item> actual = sut.readAllByNameContaining(pageRequest, name);

            // then
            assertEquals(itemPage, actual);
        }

        @Test
        @DisplayName("카테고리와 무관하게 검색 실패")
        void readAllByNameContainingTest02() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);
            var name = "name01";

            var expected = makeEmptyPageItem();

            given(itemRepository.findAllByNameContaining(pageRequest, name)).willReturn(expected);

            // when
            final Page<Item> actual = sut.readAllByNameContaining(pageRequest, name);

            // then
            assertEquals(expected, actual);
        }
    }


    @Nested
    @DisplayName("readAllByNameContainingAndCategoryId 테스트")
    class ReadAllByNameContainingAndCategoryId {
        @Test
        @DisplayName("카테고리 안에서 검색 성공")
        void readAllByNameContainingTest01() {
            // given
            var page = 0;
            var size = 10;
            var categoryId = 2L;
            var pageRequest = PageRequest.of(page, size);
            var name = "name07";

            var itemList = List.of(makeItem(33L), makeItem(44L));

            var itemPage = new PageImpl<>(itemList, pageRequest, itemList.size());
            given(itemRepository.findAllByNameContainingAndCategoryId(pageRequest, name, categoryId))
                    .willReturn(itemPage);

            // when
            final Page<Item> actual =
                    sut.readAllByNameContainingAndCategoryId(pageRequest, name, categoryId);

            // then
            assertEquals(itemPage, actual);
        }

        @Test
        @DisplayName("카테고리 안에서 검색 실패")
        void readAllByNameContainingTest02() {
            // given
            var page = 0;
            var size = 10;
            var categoryId = 2L;
            var pageRequest = PageRequest.of(page, size);
            var name = "name9999";

            var expected = makeEmptyPageItem();

            given(itemRepository.findAllByNameContainingAndCategoryId(pageRequest, name, categoryId))
                    .willReturn(expected);

            // when
            final Page<Item> actual =
                    sut.readAllByNameContainingAndCategoryId(pageRequest, name, categoryId);

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
            given(itemRepository.count()).willReturn(count);

            // when
            final Long actual = sut.total();

            // then
            assertEquals(count, actual);
        }
    }


    @Nested
    @DisplayName("totalByNameContaining 테스트")
    class TotalByNameContainingTest {
        @Test
        @DisplayName("카테고리 아이디로 검색한 상품의 개수 세기")
        void readSearchCountTest01() {
            // given
            var count = 1L;
            var searchTitle = "searchTitle";
            given(itemRepository.countByNameContaining(searchTitle)).willReturn(count);

            // when
            final Long actual = sut.totalByNameContaining(searchTitle);

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
            var itemId = 11L;
            var item = makeItem(itemId);
            given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

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
            given(itemRepository.findById(itemId)).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.remove(itemId));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.PRODUCT_NOT_FOUND);
        }
    }

    private Page<Item> makeEmptyPageItem() {
        return new PageImpl<>(Collections.emptyList());
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

    private Item makeItem(Long itemId) {
        return Item.builder()
                .id(itemId)
                .name("name")
                .price(10000)
                .quantity(1)
                .soldCount(1)
                .description("description")
                .itemStatus(ItemStatus.READY)
                .category(new Category())
                .build();
    }
}