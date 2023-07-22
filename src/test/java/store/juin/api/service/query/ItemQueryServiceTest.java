package store.juin.api.service.query;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import store.juin.api.domain.entity.Category;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.entity.ItemImage;
import store.juin.api.domain.entity.PersonalColor;
import store.juin.api.domain.enums.ItemStatus;
import store.juin.api.domain.response.ItemResponse;
import store.juin.api.exception.Msg;
import store.juin.api.handler.QueryTransactional;
import store.juin.api.repository.jpa.ItemRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ItemQueryServiceTest {
    @InjectMocks
    private ItemQueryService sut;

    @Spy
    private QueryTransactional queryTransactional;

    @Mock
    private ItemRepository itemRepository;

    @Nested
    @DisplayName("readById 테스트")
    class RetrieveByIdTest {
        @Test
        @DisplayName("itemId로 읽기 성공")
        void readByIdTest01() {
            // given
            var itemId = 1L;
            var itemImageList = makeItemImageList(99L);
            var item = makeItem(itemId, itemImageList);

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
            given(itemRepository.findById(anyLong())).willThrow(new EntityNotFoundException(Msg.ITEM_NOT_FOUND));

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readById(itemId));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ITEM_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readAllByIdList 테스트")
    class RetrieveAllByIdListTest {
        @Test
        @DisplayName("상품 리스트가 하나 이상일 때")
        void ReadAllByIdListTest01() {
            // given
            var itemIdList = List.of(1L, 2L);
            var itemList = List.of(
                    makeItem(1L, makeItemImageList(1L)),
                    makeItem(2L, makeItemImageList(2L))
            );

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
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ITEM_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readAll 테스트")
    class RetrieveAllTest {
        @Test
        @DisplayName("등록된 모든 상품 읽기 성공")
        void readAllTest01() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var itemId = 33L;
            var itemList = List.of(makeItem(itemId, makeItemImageList(24L)));

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
    class RetrieveAllByCategoryIdTest {
        @Test
        @DisplayName("카테고리 내에서 전체 상품 읽기 성공")
        void readAllByCategoryIdTest01() {
            // given
            var categoryId = 1L;
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var itemId = 342L;
            var itemList = List.of(makeItem(itemId, makeItemImageList(1L)));

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
    @DisplayName("search 테스트")
    class SearchTest {
        @Test
        @DisplayName("personalColor가 있을 때")
        void searchTest01() {
            // given
            var page = 0;
            var size = 10;
            var pageable = PageRequest.of(page, size);
            var personColor = PersonalColor.SPRING;

            var itemImageList = makeItemImageList(10L);
            var item = makeItem(1L, itemImageList);
            var itemList = List.of(item);

            given(itemRepository.findAllByPersonalColor(pageable, personColor))
                    .willReturn(new PageImpl<>(itemList, pageable, itemList.size()));

            var itemReadResponseList = makeItemReadResponseList(itemList, itemImageList);
            var expected = new PageImpl<>(itemReadResponseList, pageable, 1);

            // when
            final Page<ItemResponse.Read> actual = sut.search(pageable, null, null, personColor);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("검색어가 있을 때")
        void searchTest02() {
            // given
            var page = 0;
            var size = 10;
            var pageable = PageRequest.of(page, size);
            var searchTitle = "이게 검색어다!";

            var itemImageList = makeItemImageList(20L);
            var item = makeItem(1L, itemImageList);
            var itemList = List.of(item);

            given(itemRepository.findByNameContainingAndCategoryId(pageable, searchTitle, null))
                    .willReturn(Optional.of(new PageImpl<>(itemList, pageable, itemList.size())));

            var itemReadResponseList = makeItemReadResponseList(itemList, itemImageList);
            var expected = new PageImpl<>(itemReadResponseList, pageable, 1);

            // when
            final Page<ItemResponse.Read> actual = sut.search(pageable, searchTitle, null, null);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("categoryId가 있을 때")
        void searchTest03() {
            // given
            var page = 0;
            var size = 10;
            var pageable = PageRequest.of(page, size);
            var categoryId = 10L;

            var itemImageList = makeItemImageList(20L);
            var item = Item.builder()
                    .id(1L)
                    .name("name")
                    .price(10000)
                    .quantity(1)
                    .soldCount(1)
                    .description("description")
                    .itemStatus(ItemStatus.READY)
                    .category(makeCategory())
                    .itemImageList(itemImageList)
                    .build();
            var itemList = List.of(item);

            given(itemRepository.findByNameContainingAndCategoryId(pageable, null, categoryId))
                    .willReturn(Optional.of(new PageImpl<>(itemList, pageable, itemList.size())));

            var expected = makeItemReadResponseList(itemList, itemImageList);

            // when
            final Page<ItemResponse.Read> actual = sut.search(pageable, null, categoryId, null);

            // then
            assertIterableEquals(expected, actual);
        }


        @Test
        @DisplayName("특정 카테고리에서 상품 검색할 때")
        void searchTest04() {
            // given
            var page = 0;
            var size = 10;
            var pageable = PageRequest.of(page, size);
            var searchTitle = "이게 검색어다!";
            var categoryId = 10L;

            var itemImageList = makeItemImageList(20L);
            var item = Item.builder()
                    .id(1L)
                    .name("name")
                    .price(10000)
                    .quantity(1)
                    .soldCount(1)
                    .description("description")
                    .itemStatus(ItemStatus.READY)
                    .category(makeCategory())
                    .itemImageList(itemImageList)
                    .build();
            var itemList = List.of(item);

            given(itemRepository.findByNameContainingAndCategoryId(pageable, searchTitle, categoryId))
                    .willReturn(Optional.of(new PageImpl<>(itemList, pageable, itemList.size())));

            var expected = makeItemReadResponseList(itemList, itemImageList);

            // when
            final Page<ItemResponse.Read> actual = sut.search(pageable, searchTitle, categoryId, null);

            // then
            assertIterableEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("display 테스트")
    class DisplayTest {
        @Test
        @DisplayName("item 전체 조회")
        void test() {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var itemId = 33L;
            var itemList = List.of(makeItem(itemId, makeItemImageList(24L)));
            var itemImageList = makeItemImageList(20L);

            var itemPage = new PageImpl<>(itemList, pageRequest, itemList.size());
            var expected = makeItemReadResponseList(itemList, itemImageList);


            given(itemRepository.findAll(pageRequest)).willReturn(itemPage);

            // when
            final Page<ItemResponse.Read> actual = sut.display(pageRequest);

            // then
            assertIterableEquals(expected, actual);
        }

    }

    private List<ItemImage> makeItemImageList(Long itemImageId) {
        return List.of(
                makeItemImage(itemImageId
                        , "cat.jpg"
                        , "cat.jpg"
                        , "/cat.jpg"
                        , true
                ),

                makeItemImage(itemImageId + 10L
                        , "dog.jpg"
                        , "dog.jpg"
                        , "/dog.jpg"
                        , false
                )
        );
    }

    private List<ItemResponse.Read> makeItemReadResponseList(List<Item> itemList,
                                                             List<ItemImage> itemImageList) {

        return itemList.stream()
                .map(image -> ItemResponse.Read.of(image, itemImageList))
                .collect(Collectors.toList());
    }

    private ItemImage makeItemImage(Long id
            , String name
            , String originName
            , String imageUrl
            , Boolean thumbnail) {

        return ItemImage.builder()
                .id(id)
                .name(name)
                .originName(originName)
                .imageUrl(imageUrl)
                .thumbnail(thumbnail)
                .build();
    }


    private Page<Item> makeEmptyPageItem() {
        return new PageImpl<>(Collections.emptyList());
    }

    private Category makeCategory() {
        return Category.builder()
                .id(1L)
                .build();
    }

    private Item makeItem(Long itemId, List<ItemImage> itemImageList) {
        return Item.builder()
                .id(itemId)
                .name("name")
                .price(10000)
                .quantity(1)
                .soldCount(1)
                .description("description")
                .itemStatus(ItemStatus.READY)
                .category(makeCategory())
                .itemImageList(itemImageList)
                .build();
    }
}