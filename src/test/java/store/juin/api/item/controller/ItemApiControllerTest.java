package store.juin.api.item.controller;

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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import store.juin.api.item.enumeration.ItemStatus;
import store.juin.api.item.model.response.ItemRetrieveResponse;
import store.juin.api.item.service.query.ItemQueryService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.juin.api.common.EndPoint.PORT;
import static store.juin.api.common.EntityUtil.makeItem;
import static store.juin.api.util.CharterUtil.DOT;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class ItemApiControllerTest {
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ItemApiController sut;

    @Mock
    private ItemQueryService itemQueryService;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(sut)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers(new ViewResolver() {
                    @Override
                    public View resolveViewName(String viewName, Locale locale) throws Exception {
                        return new MappingJackson2JsonView();
                    }
                })
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .and().uris().withPort(PORT)         // 포트 설정
                        .and().operationPreprocessors()
                        .withRequestDefaults(prettyPrint())   // request 본문을 예쁘게 출력
                        .withResponseDefaults(prettyPrint())) // response 본문을 예쁘게 출력
                .build();
    }

    @Nested
    @DisplayName("GET /api/items/{itemId}")
    class RetrieveOneTest {
        @Test
        @DisplayName("(성공) 상품 하나를 읽어온다.")
        void retrieveOneTest01() throws Exception {
            // given
            var itemId = 1L;
            var item = makeItem(itemId, "이게 제품이다!!!");

            given(itemQueryService.readById(itemId)).willReturn(item);

            // when
            final ResultActions actual = mockMvc.perform(get("/api/items/{itemId}", itemId)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/items/success/retrieveOne"

                            , pathParameters(
                                    parameterWithName("itemId").description("아이템 아이디")
                            )

                            , responseFields(
                                    fieldWithPath("apiStatus").type(Integer.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data.id").type(Integer.class).description("상품 id")
                                    , fieldWithPath("data.name").type(String.class).description("상품 이름")
                                    , fieldWithPath("data.price").type(Integer.class).description("상품 가격")
                                    , fieldWithPath("data.quantity").type(Integer.class).description("남아있는 상품 개수")
                                    , fieldWithPath("data.soldCount").type(Integer.class).description("여태 판매된 상품 개수")
                                    , fieldWithPath("data.description").type(String.class).description("상품 설명")
                                    , fieldWithPath("data.itemStatus").type(ItemStatus.class).description("아이템 상태")
                                    , fieldWithPath("data.itemImageList[].imageName").type(String.class).description("원본 이미지 파일명을 통해 새로 만든 이미지 파일명")
                                    , fieldWithPath("data.itemImageList[].originName").type(String.class).description("원본 오리지날 이름")
                                    , fieldWithPath("data.itemImageList[].imageUrl").type(String.class).description("이미지 업로드 경로")
                                    , fieldWithPath("data.itemImageList[].thumbnail").type(boolean.class).description("해당 이미지는 썸네일인가?")
                                    , fieldWithPath("data.itemImageList[].representative").type(boolean.class).description("해당 이미지는 대표 썸네일인가?")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보"))
                    ));
        }

//        @Test
//        @DisplayName("존재하지 않는 상품 조회 시도로 인한 조회 실패")
//        public void RetrieveOneTest02() throws Exception {
//            given(mockItemQueryService.readById(1L)).willThrow(new EntityNotFoundException());
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get("/api/items/{itemId}", 1L)
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
//        }

    }

    @Nested
    @DisplayName("GET /api/items")
    class RetrieveAllTest {
        @Test
        @DisplayName("(성공) 전체 상품 목록 읽기")
        void retrieveAllTest01() throws Exception {
            // given
            var itemId = 1L;
            var itemList = List.of(makeItem(itemId, "이게 제품이다!!!"), makeItem(itemId + 1, "이것도 제품이다!!!"));

            var page = 0;
            var size = 10;
            final PageRequest pageable = PageRequest.of(page, size);
            var read = itemList.stream()
                    .map(item -> ItemRetrieveResponse.of(item, item.getItemImageList()))
                    .collect(Collectors.toList());

            var response = new PageImpl<>(read, pageable, read.size());
            given(itemQueryService.display(pageable)).willReturn(response);

            // when
            final ResultActions actual = mockMvc.perform(get("/api/items")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .param("page", String.valueOf(page))
                    .param("size", String.valueOf(size)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/items/success/retrieveAll"
                            , requestParameters(
                                        parameterWithName("page").description("페이지 번호")
                                    , parameterWithName("size").description("몇 개의 페이지를 가져올 것인지")
                            )

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data.content[].id").type(Long.class).description("상품 id")
                                    , fieldWithPath("data.content[].name").type(String.class).description("상품명")
                                    , fieldWithPath("data.content[].price").type(Integer.class).description("상품 가격")
                                    , fieldWithPath("data.content[].quantity").type(Integer.class).description("상품 개수")
                                    , fieldWithPath("data.content[].soldCount").type(Integer.class).description("여태까지 판매된 상품 개수")
                                    , fieldWithPath("data.content[].description").type(String.class).description("상품 설명")
                                    , fieldWithPath("data.content[].itemStatus").type(ItemStatus.class).description("상품 상태")
                                    , fieldWithPath("data.content[].itemImageList[].imageName").type(String.class).description("상품 이미지 이름")
                                    , fieldWithPath("data.content[].itemImageList[].originName").type(String.class).description("상품 파일 이름")
                                    , fieldWithPath("data.content[].itemImageList[].imageUrl").type(String.class).description("상품 이미지 경로")
                                    , fieldWithPath("data.content[].itemImageList[].thumbnail").type(Boolean.class).description("상품 썸네일 여부")
                                    , fieldWithPath("data.content[].itemImageList[].representative").type(Boolean.class).description("상품 대표 이미지인가?")

                                    , fieldWithPath("data.pageable.sort.sorted").type(boolean.class).description("정렬 여부")
                                    , fieldWithPath("data.pageable.sort.unsorted").type(boolean.class).description("정렬 여부")
                                    , fieldWithPath("data.pageable.sort.empty").type(boolean.class).description("정렬 여부")
                                    , fieldWithPath("data.pageable.offset").type(int.class).description("페이지 시작 번호")
                                    , fieldWithPath("data.pageable.pageSize").type(int.class).description("페이지 사이즈")
                                    , fieldWithPath("data.pageable.pageNumber").type(int.class).description("페이지 번호")
                                    , fieldWithPath("data.pageable.paged").type(boolean.class).description("페이지 여부")
                                    , fieldWithPath("data.pageable.unpaged").type(boolean.class).description("페이지 여부")
                                    , fieldWithPath("data.totalElements").type(int.class).description("총 요소 개수")
                                    , fieldWithPath("data.totalPages").type(int.class).description("총 페이지 개수")
                                    , fieldWithPath("data.last").type(boolean.class).description("마지막 페이지 여부")
                                    , fieldWithPath("data.first").type(boolean.class).description("첫 페이지 여부")
                                    , fieldWithPath("data.sort.sorted").type(boolean.class).description("정렬 여부")
                                    , fieldWithPath("data.sort.unsorted").type(boolean.class).description("정렬 여부")
                                    , fieldWithPath("data.sort.empty").type(boolean.class).description("정렬 여부")
                                    , fieldWithPath("data.numberOfElements").type(int.class).description("요소 개수")
                                    , fieldWithPath("data.size").type(int.class).description("페이지 사이즈")
                                    , fieldWithPath("data.number").type(int.class).description("페이지 번호")
                                    , fieldWithPath("data.empty").type(boolean.class).description("빈 페이지 여부")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("라전 정보"))

                    ));
        }
    }
//
//    @Nested
//    @DisplayName("상품 검색하기")
//    class SearchTest {
//        @Test
//        @DisplayName("성공")
//        void searchTest01() throws Exception {
//            // given
//            var page = 0;
//            var size = 10;
//
//            var pageRequest = PageRequest.of(page, size);
//            var searchTitle = "title";
//
//            var itemList = getItemList();
//            List<ItemResponse.Read> response = new ArrayList<>();
//            for (Item item : itemList) {
//                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
//            }
//
//            given(mockItemRelationService.search(pageRequest, searchTitle, null)).willReturn(response);
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get("/api/items/search")
//                            .param("page", Integer.toString(page))
//                            .param("size", Integer.toString(size))
//                            .param("name", searchTitle)
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//        }
//
//        @Test
//        @DisplayName("카테고리 내 상품 검색 성공")
//        void searchTest02() throws Exception {
//            // given
//            var page = 0;
//            var size = 10;
//
//            var pageRequest = PageRequest.of(page, size);
//            var searchTitle = "title";
//            var categoryId = 1L;
//
//            var itemList = getItemList();
//            List<ItemResponse.Read> response = new ArrayList<>();
//            for (Item item : itemList) {
//                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
//            }
//
//            given(mockItemRelationService.search(pageRequest, searchTitle, categoryId)).willReturn(response);
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get("/api/items/search")
//                            .param("page", Integer.toString(page))
//                            .param("size", Integer.toString(size))
//                            .param("name", searchTitle)
//                            .param("categoryId", Long.toString(categoryId))
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//        }
//
//        @Test
//        @DisplayName("존재하지 않는 상품 이미지")
//        public void retrieveOneTest04() throws Exception {
//            // given
//            var page = 0;
//            var size = 10;
//            var itemList = getItemList();
//            var searchTitle = "title";
//
//            var pageRequest = PageRequest.of(page, size);
//            List<ItemResponse.Read> response = new ArrayList<>();
//            for (Item item : itemList) {
//                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
//            }
//
//            given(mockItemRelationService.search(pageRequest, searchTitle, null)).willThrow(new EntityNotFoundException());
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get("/api/items/search")
//                            .param("page", Integer.toString(page))
//                            .param("size", Integer.toString(size))
//                            .param("name", searchTitle)
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
//        }
//    }
//
//
//        @Test
//        @DisplayName("카테고리 별 상품 읽기 성공")
//        public void retrieveOneTest02() throws Exception {
//            // given
//            var categoryId = 1L;
//            var page = 0;
//            var size = 10;
//            var itemList = getItemList();
//
//            var pageRequest = PageRequest.of(page, size);
//            List<ItemResponse.Read> response = new ArrayList<>();
//            for (Item item : itemList) {
//                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
//            }
//
//            given(mockItemRelationService.display(pageRequest, categoryId)).willReturn(response);
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get(ITEM_END_POINT)
//                            .param("page", Integer.toString(page))
//                            .param("size", Integer.toString(size))
//                            .param("categoryId", Long.toString(categoryId))
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//        }
//
//        @Test
//        @DisplayName("카테고리 별 상품 읽기 성공")
//        public void retrieveOneTest03() throws Exception {
//            // given
//            var categoryId = 1L;
//            var page = 0;
//            var size = 10;
//            var itemList = getItemList();
//
//            var pageRequest = PageRequest.of(page, size);
//            List<ItemResponse.Read> response = new ArrayList<>();
//            for (Item item : itemList) {
//                response.add(ItemResponse.Read.of(item, item.getItemImageList()));
//            }
//
//            given(mockItemRelationService.display(pageRequest, categoryId)).willReturn(response);
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get(ITEM_END_POINT)
//                            .param("page", Integer.toString(page))
//                            .param("size", Integer.toString(size))
//                            .param("categoryId", Long.toString(categoryId))
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//        }
//    }
//
//
//    private List<Item> getItemList() {
//        List<ItemImage> itemImageList = new ArrayList<>();
//        ItemImage.ItemImageBuilder itemImageBuilder = ItemImage.builder()
//                .id(1L)
//                .name("name")
//                .originName("originName")
//                .imageUrl("imageUrl")
//                .thumbnail(true);
//
//        List<OrderItem> orderItemList = new ArrayList<>();
//
//        List<Item> itemList = new ArrayList<>();
//        Item item = getItemByInfo(itemImageList, orderItemList);
//        itemList.add(item);
//
//        ItemImage itemImage = itemImageBuilder
//                .item(item)
//                .build();
//        itemImageList.add(itemImage);
//
//        return itemList;
//    }
//
}