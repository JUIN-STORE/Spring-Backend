package com.ecommerce.backend.controller;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.entity.OrderItem;
import com.ecommerce.backend.domain.enums.ItemStatus;
import com.ecommerce.backend.domain.request.ItemRequest;
import com.ecommerce.backend.domain.response.ItemResponse;
import com.ecommerce.backend.service.ItemService;
import com.ecommerce.backend.relation.ItemRelationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemApiControllerTest {
    private static final String ITEM_END_POINT = "/api/items";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private ItemService mockItemService;

    @Mock
    private ItemRelationService mockItemRelationService;

    @InjectMocks
    private ItemApiController sut;


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

            var thumbnail = new MockMultipartFile(
                    "thumbnail",
                    "thumbnail.jpg",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new byte[0]);

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

            given(mockItemService.add(any(), any(), any())).willReturn(1L);

            // when
            final ResultActions perform = mockMvc.perform(
                    multipart(ITEM_END_POINT + "/seller/register")
                            .file(request)
                            .file(thumbnail)
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

            var thumbnailFile = new MockMultipartFile(
                    "thumbnail",
                    "test.jpg",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new byte[0]);

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

            given(mockItemService.add(any(), any(), any())).willThrow(new EntityNotFoundException());

            // whenㅔ
            final ResultActions perform = mockMvc.perform(
                    multipart(ITEM_END_POINT + "/seller/register")
                            .file(request)
                            .file(thumbnailFile)
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

            var thumbnailFile = new MockMultipartFile(
                    "thumbnail",
                    "test.jpg",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new byte[0]);

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

            given(mockItemService.add(any(), any(), any()))
                    .willThrow(new FileNotFoundException());

            // when
            final ResultActions perform = mockMvc.perform(
                    multipart(ITEM_END_POINT + "/seller/register")
                            .file(request)
                            .file(thumbnailFile)
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
            var item = getItemByInfo(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
            given(mockItemService.readById(1L)).willReturn(item);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT + "/seller/{itemId}", 1L)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("존재하지 않는 상품")
        public void adminReadTest02() throws Exception {
            // given
            given(mockItemService.readById(1L)).willThrow(new EntityNotFoundException());

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT + "/seller/{itemId}", 1L)
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
            given(mockItemService.remove(1L)).willReturn(anyLong());

            // when
            final ResultActions perform = mockMvc.perform(
                    delete(ITEM_END_POINT + "/seller/{itemId}", 1L)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("존재하지 않는 상품 삭제 시도로 인한 삭제 실패")
        public void adminRemoveTest02() throws Exception {
            // given
            given(mockItemService.remove(1L)).willThrow(new EntityNotFoundException());

            // when
            final ResultActions perform = mockMvc.perform(
                    delete(ITEM_END_POINT + "/seller/{itemId}", 1L)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
        }
    }

    @Nested
    @DisplayName("상품 검색하기")
    class SearchTest {
        @Test
        @DisplayName("성공")
        void searchTest01() throws Exception {
            // given
            var page = 0;
            var size = 10;

            var pageRequest = PageRequest.of(page, size);
            var searchTitle = "title";

            var itemList = getItemList();
            List<ItemResponse.Read> response = new ArrayList<>();
            for (Item item : itemList) {
                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
            }

            given(mockItemRelationService.search(pageRequest, searchTitle, null)).willReturn(response);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT + "/search")
                            .param("page", Integer.toString(page))
                            .param("size", Integer.toString(size))
                            .param("name", searchTitle)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("카테고리 내 상품 검색 성공")
        void searchTest02() throws Exception {
            // given
            var page = 0;
            var size = 10;

            var pageRequest = PageRequest.of(page, size);
            var searchTitle = "title";
            var categoryId = 1L;

            var itemList = getItemList();
            List<ItemResponse.Read> response = new ArrayList<>();
            for (Item item : itemList) {
                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
            }

            given(mockItemRelationService.search(pageRequest, searchTitle, categoryId)).willReturn(response);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT + "/search")
                            .param("page", Integer.toString(page))
                            .param("size", Integer.toString(size))
                            .param("name", searchTitle)
                            .param("categoryId", Long.toString(categoryId))
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("존재하지 않는 상품 이미지")
        public void retrieveOneTest04() throws Exception {
            // given
            var page = 0;
            var size = 10;
            var itemList = getItemList();
            var searchTitle = "title";

            var pageRequest = PageRequest.of(page, size);
            List<ItemResponse.Read> response = new ArrayList<>();
            for (Item item : itemList) {
                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
            }

            given(mockItemRelationService.search(pageRequest, searchTitle, null)).willThrow(new EntityNotFoundException());

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT + "/search")
                            .param("page", Integer.toString(page))
                            .param("size", Integer.toString(size))
                            .param("name", searchTitle)
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
            var item = getItemByInfo(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
            given(mockItemService.readById(1L)).willReturn(item);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT + "/{itemId}", 1L)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("존재하지 않는 상품 조회 시도로 인한 조회 실패")
        public void RetrieveOneTest02() throws Exception {
            given(mockItemService.readById(1L)).willThrow(new EntityNotFoundException());

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT + "/{itemId}", 1L)
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
            var itemList = getItemList();

            var pageRequest = PageRequest.of(page, size);
            List<ItemResponse.Read> response = new ArrayList<>();
            for (Item item : itemList) {
                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
            }

            given(mockItemRelationService.display(pageRequest, null)).willReturn(response);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT)
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
            var categoryId = 1L;
            var page = 0;
            var size = 10;
            var itemList = getItemList();

            var pageRequest = PageRequest.of(page, size);
            List<ItemResponse.Read> response = new ArrayList<>();
            for (Item item : itemList) {
                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
            }

            given(mockItemRelationService.display(pageRequest, categoryId)).willReturn(response);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT)
                            .param("page", Integer.toString(page))
                            .param("size", Integer.toString(size))
                            .param("categoryId", Long.toString(categoryId))
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("카테고리 별 상품 읽기 성공")
        public void retrieveOneTest03() throws Exception {
            // given
            var categoryId = 1L;
            var page = 0;
            var size = 10;
            var itemList = getItemList();

            var pageRequest = PageRequest.of(page, size);
            List<ItemResponse.Read> response = new ArrayList<>();
            for (Item item : itemList) {
                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
            }

            given(mockItemRelationService.display(pageRequest, categoryId)).willReturn(response);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT)
                            .param("page", Integer.toString(page))
                            .param("size", Integer.toString(size))
                            .param("categoryId", Long.toString(categoryId))
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
            given(mockItemService.total()).willReturn(count);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT + "/count"));

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
            given(mockItemService.totalByNameContaining(searchTitle)).willReturn(count);

            // when
            final ResultActions perform = mockMvc.perform(
                    get(ITEM_END_POINT + "/search/count")
                            .param("name", searchTitle));

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

    private List<Item> getItemList() {
        List<ItemImage> itemImageList = new ArrayList<>();
        ItemImage.ItemImageBuilder itemImageBuilder = ItemImage.builder()
                .id(1L)
                .name("name")
                .originName("originName")
                .imageUrl("imageUrl")
                .thumbnail(true);

        List<OrderItem> orderItemList = new ArrayList<>();

        List<Item> itemList = new ArrayList<>();
        Item item = getItemByInfo(itemImageList, orderItemList);
        itemList.add(item);

        ItemImage itemImage = itemImageBuilder
                .item(item)
                .build();
        itemImageList.add(itemImage);

        return itemList;
    }

    private static Item getItemByInfo(List<ItemImage> itemImageList,
                                         List<OrderItem> orderItemList) {
        Item item = new Item(
                1L,
                "item",
                10000,
                1,
                0,
                "description",
                ItemStatus.SELL,
                new Category(),
                itemImageList,
                orderItemList);
        return item;
    }

    private static ItemRequest.Create getCreateRequest() {
        return new ItemRequest.Create()
                .setCategoryId(1L)
                .setName("item")
                .setPrice(10000)
                .setQuantity(1)
                .setDescription("description");
    }
}