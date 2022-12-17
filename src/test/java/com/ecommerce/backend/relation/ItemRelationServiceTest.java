package com.ecommerce.backend.relation;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.enums.ItemStatus;
import com.ecommerce.backend.domain.response.ItemResponse;
import com.ecommerce.backend.service.ItemImageService;
import com.ecommerce.backend.service.ItemService;
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
class ItemRelationServiceTest {
    @InjectMocks
    ItemRelationService sut;

    @Mock
    ItemService itemService;

    @Mock
    ItemImageService itemImageService;

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

            var item = makeItem(1L);
            var itemList = List.of(item);

            var itemImageList = makeItemImageList(20L, item);

            given(itemImageService.readAllByItemId(List.of(1L))).willReturn(itemImageList);
            given(itemService.readAll(pageable))
                    .willReturn(new PageImpl<>(itemList, pageable, itemList.size()));

            var expected = makeItemReadResponseList(itemList, itemImageList);

            // when
            final List<ItemResponse.Read> actual = sut.display(pageable, null);

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

            var item = makeItem(1L);
            var itemList = List.of(item);

            var itemImageList = makeItemImageList(20L, item);

            given(itemImageService.readAllByItemId(List.of(1L))).willReturn(itemImageList);
            given(itemService.readAllByCategoryId(pageable, categoryId))
                    .willReturn(new PageImpl<>(itemList, pageable, itemList.size()));

            var expected = makeItemReadResponseList(itemList, itemImageList);

            // when
            final List<ItemResponse.Read> actual = sut.display(pageable, categoryId);

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

            var item = makeItem(1L);
            var itemList = List.of(item);

            var itemImageList = makeItemImageList(20L, item);

            given(itemImageService.readAllByItemId(List.of(1L))).willReturn(itemImageList);
            given(itemService.readAllByNameContaining(pageable, searchTitle))
                    .willReturn(new PageImpl<>(itemList, pageable, itemList.size()));

            var expected = makeItemReadResponseList(itemList, itemImageList);

            // when
            final List<ItemResponse.Read> actual = sut.search(pageable, searchTitle, null);

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

            var item = makeItem(1L);
            var itemList = List.of(item);

            var itemImageList = makeItemImageList(20L, item);

            given(itemImageService.readAllByItemId(List.of(1L))).willReturn(itemImageList);
            given(itemService.readAllByNameContainingAndCategoryId(pageable, searchTitle, categoryId))
                    .willReturn(new PageImpl<>(itemList, pageable, itemList.size()));

            var expected = makeItemReadResponseList(itemList, itemImageList);

            // when
            final List<ItemResponse.Read> actual = sut.search(pageable, searchTitle, categoryId);

            // then
            assertIterableEquals(expected, actual);
        }

    }

    @Nested
    @DisplayName("makeItemReadResponse 테스트")
    class MakeItemReadResponseTest {
        @Test
        @DisplayName("상품에 2개 이상의 이미지가 있을 때")
        void makeItemReadResponseTest01() throws Exception {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var itemIdList = List.of(1L);
            var item = makeItem(1L);
            var itemList = List.of(item);

            var itemPage = new PageImpl<>(itemList, pageRequest, itemList.size());

            var itemImageList = makeItemImageList(20L, item);
            given(itemImageService.readAllByItemId(itemIdList)).willReturn(itemImageList);

            var expected = makeItemReadResponseList(itemList, itemImageList);

            // when
            Method method = ItemRelationService.class.getDeclaredMethod("makeItemReadResponseList", Page.class);
            method.setAccessible(true);
            final List<ItemResponse.Read> actual = (List<ItemResponse.Read>) method.invoke(sut, itemPage);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("상품에 1개 이미지만 있을 때")
        void makeItemReadResponseTest02() throws Exception {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var itemIdList = List.of(1L);
            var item = makeItem(1L);
            var itemList = List.of(item);

            var itemPage = new PageImpl<>(itemList, pageRequest, itemList.size());

            var itemImageList = List.of(
                    makeItemImage(88L
                            , "cat.jpg"
                            , "cat.jpg"
                            , "/cat.jpg"
                            , true
                            , item
                    )
            );
            given(itemImageService.readAllByItemId(itemIdList)).willReturn(itemImageList);

            var expected = makeItemReadResponseList(itemList, itemImageList);

            // when
            Method method = ItemRelationService.class.getDeclaredMethod("makeItemReadResponseList", Page.class);
            method.setAccessible(true);
            final List<ItemResponse.Read> actual = (List<ItemResponse.Read>) method.invoke(sut, itemPage);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("상품에 이미지가 하나도 없을 떼")
        void makeItemReadResponseTest03() throws Exception {
            // given
            var page = 0;
            var size = 10;
            var pageRequest = PageRequest.of(page, size);

            var itemIdList = List.of(1L);
            var item = makeItem(1L);
            var itemList = List.of(item);

            var itemPage = new PageImpl<>(itemList, pageRequest, itemList.size());

            given(itemImageService.readAllByItemId(itemIdList)).willReturn(new ArrayList<>());

            var expected = makeItemReadResponseList(itemList, new ArrayList<>());

            // when
            Method method = ItemRelationService.class.getDeclaredMethod("makeItemReadResponseList", Page.class);
            method.setAccessible(true);
            final List<ItemResponse.Read> actual = (List<ItemResponse.Read>) method.invoke(sut, itemPage);

            // then
            assertIterableEquals(expected, actual);
        }
    }


    private List<ItemImage> makeItemImageList(Long itemImageId, Item item) {
        return List.of(
                makeItemImage(itemImageId
                        , "cat.jpg"
                        , "cat.jpg"
                        , "/cat.jpg"
                        , true
                        , item
                ),

                makeItemImage(itemImageId + 10L
                        , "dog.jpg"
                        , "dog.jpg"
                        , "/dog.jpg"
                        , false
                        , item
                )
        );
    }

    private List<ItemResponse.Read> makeItemReadResponseList(List<Item> itemList
            , List<ItemImage> itemImageList) {

        return itemList.stream()
                .map(image -> ItemResponse.Read.of(image, itemImageList))
                .collect(Collectors.toList());
    }

    private ItemImage makeItemImage(Long id
            , String name
            , String originName
            , String imageUrl
            , Boolean isThumbnail
            , Item item) {

        return ItemImage.builder()
                .id(id)
                .name(name)
                .originName(originName)
                .imageUrl(imageUrl)
                .thumbnail(isThumbnail)
                .item(item)
                .build();
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