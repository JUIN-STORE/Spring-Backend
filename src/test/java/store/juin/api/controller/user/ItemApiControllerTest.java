//package store.juin.api.controller.user;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.restdocs.RestDocumentationContextProvider;
//import org.springframework.restdocs.RestDocumentationExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import store.juin.api.controller.user.ItemApiController;
//import store.juin.api.domain.enums.ItemStatus;
//import store.juin.api.domain.request.ItemRequest;
//import store.juin.api.service.query.ItemQueryService;
//
//import java.time.ZonedDateTime;
//
//import static org.mockito.BDDMockito.given;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
//import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
//import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static store.juin.api.domain.EndPoint.PORT;
//import static store.juin.api.domain.EntityUtil.makeItem;
//
//@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
//class ItemApiControllerTest {
//    private static final String API_ITEM = "/api/items";
//
//    private static final String ITEM_ID = API_ITEM + "/{itemId}";
//    private MockMvc mockMvc;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @InjectMocks
//    private ItemApiController sut;
//
//    @Mock
//    private ItemQueryService itemQueryService;
//
//    @BeforeEach
//    void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
//        mockMvc = MockMvcBuilders
//                .standaloneSetup(sut)
//                .apply(documentationConfiguration(restDocumentationContextProvider)
//                        .operationPreprocessors()
//                        .and().uris().withPort(PORT)         // 포트 설정
//                        .and().operationPreprocessors()
//                        .withRequestDefaults(prettyPrint())   // request 본문을 예쁘게 출력
//                        .withResponseDefaults(prettyPrint())) // response 본문을 예쁘게 출력
//                .build();
//    }
//
//    @Nested
//    @DisplayName("@GetMapping(" + ")")
//    class RetrieveOneTest {
//        @Test
//        @DisplayName("상품 읽기 성공")
//        void retrieveOneTest01() throws Exception {
//            // given
//            var itemId = 1L;
//            var item = makeItem(itemId, "이게 제품이다!!!");
//
//            given(itemQueryService.readById(itemId)).willReturn(item);
//
//            // when
//            final ResultActions actual = mockMvc.perform(get(ITEM_ID, itemId)
//                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));
//
//            // then
//            actual
//                    .andExpect(status().isOk())
//                    .andDo(document(ITEM_ID
//                            , pathParameters(
//                                    parameterWithName("itemId").description("아이템 아이디")
//                            )
//
//                            , responseFields(fieldWithPath("apiStatus").type(Integer.class).description("api 요청에 대한 상태")
//                                    , fieldWithPath("data[].id").type(Integer.class).description("상품 id")
//                                    , fieldWithPath("data[].name").type(String.class).description("상품 이름")
//                                    , fieldWithPath("data[].price").type(Integer.class).description("상품 가격")
//                                    , fieldWithPath("data[].quantity").type(Integer.class).description("남아있는 상품 개수")
//                                    , fieldWithPath("data[].soldCount").type(Integer.class).description("여태 판매된 상품 개수")
//                                    , fieldWithPath("data[].description").type(String.class).description("상품 설명")
//                                    , fieldWithPath("data[].itemStatus").type(ItemStatus.class).description("아이템 상태")
//                                    , fieldWithPath("data[].itemImageList[].imageName").type(String.class).description("원본 이미지 파일명을 통해 새로 만든 이미지 파일명")
//                                    , fieldWithPath("data[].itemImageList[].originName").type(String.class).description("원본 오리지날 이름")
//                                    , fieldWithPath("data[].itemImageList[].imageUrl").type(String.class).description("이미지 업로드 경로")
//                                    , fieldWithPath("data[].itemImageList[].thumbnail").type(boolean.class).description("해당 이미지는 대표 썸네일인가?")
//                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
//                                    , fieldWithPath("region").type(String.class).description("상세 주소"))
//                    ));
//        }
//
//
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get(ITEM_END_POINT + "/{itemId}", itemId)
//            );
//
//            // then
//            perform.andExpect(status().isOk());
//    }
//
//
//
//    @Nested
//    @DisplayName("상품 등록")
//    class RegisterTest {
//        @Test
//        @DisplayName("판매자 상품 등록 성공")
//        void registerTest01() throws Exception {
//            // given
//            var createRequest = makeCreateRequest();
//
//            var thumbnail = new MockMultipartFile(
//                    "thumbnail",
//                    "thumbnail.jpg",
//                    MediaType.MULTIPART_FORM_DATA_VALUE,
//                    new byte[0]);
//
//            var file = new MockMultipartFile(
//                    "fileList",
//                    "test.jpg",
//                    MediaType.MULTIPART_FORM_DATA_VALUE,
//                    new byte[0]);
//
//            var json = objectMapper.writeValueAsString(createRequest);
//            var request = new MockMultipartFile(
//                    "request",
//                    "",
//                    MediaType.APPLICATION_JSON_VALUE,
//                    json.getBytes());
//
//            given(itemQueryService.add(any(), any(), any())).willReturn(1L);
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    multipart(ITEM_END_POINT + "/seller/register")
//                            .file(request)
//                            .file(thumbnail)
//                            .file(file)
//            );
//
//            // then
//            perform.andExpect(status().isOk());
//        }
//
//        @Test
//        @DisplayName("존재하지 않는 카테고리")
//        public void registerTest02() throws Exception {
//            // given
//            var createRequest = makeCreateRequest();
//
//            var thumbnailFile = new MockMultipartFile(
//                    "thumbnail",
//                    "test.jpg",
//                    MediaType.MULTIPART_FORM_DATA_VALUE,
//                    new byte[0]);
//
//            var file = new MockMultipartFile(
//                    "fileList",
//                    "test.jpg",
//                    MediaType.MULTIPART_FORM_DATA_VALUE,
//                    new byte[0]);
//
//            var json = objectMapper.writeValueAsString(createRequest);
//            var request = new MockMultipartFile(
//                    "request",
//                    "",
//                    MediaType.APPLICATION_JSON_VALUE,
//                    json.getBytes());
//
//            given(mockItemQueryService.add(any(), any(), any())).willThrow(new EntityNotFoundException());
//
//            // whenㅔ
//            final ResultActions perform = mockMvc.perform(
//                    multipart(ITEM_END_POINT + "/seller/register")
//                            .file(request)
//                            .file(thumbnailFile)
//                            .file(file)
//            );
//
//            // then
//            perform.andExpect(status().isOk());
//            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
//        }
//
//        @Test
//        @DisplayName("파일 등록 실패")
//        public void registerTest03() throws Exception {
//            // given
//            var createRequest = makeCreateRequest();
//
//            var thumbnailFile = new MockMultipartFile(
//                    "thumbnail",
//                    "test.jpg",
//                    MediaType.MULTIPART_FORM_DATA_VALUE,
//                    new byte[0]);
//
//            var file = new MockMultipartFile(
//                    "fileList",
//                    "test.jpg",
//                    MediaType.MULTIPART_FORM_DATA_VALUE,
//                    new byte[0]);
//
//            var json = objectMapper.writeValueAsString(createRequest);
//            var request = new MockMultipartFile(
//                    "request",
//                    "",
//                    MediaType.APPLICATION_JSON_VALUE,
//                    json.getBytes());
//
//            given(mockItemQueryService.add(any(), any(), any()))
//                    .willThrow(new FileNotFoundException());
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    multipart(ITEM_END_POINT + "/seller/register")
//                            .file(request)
//                            .file(thumbnailFile)
//                            .file(file)
//            );
//
//            // then
//            perform.andExpect(status().isOk());
//            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":500"));
//        }
//
//    }
//    @Nested
//    @DisplayName("판매자 상품 읽기")
//    class AdminReadTest {
//        @Test
//        @DisplayName("판매자 상품 읽기 성공")
//        public void adminReadTest01() throws Exception {
//            // given
//            var item = getItemByInfo(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
//            given(mockItemQueryService.readById(1L)).willReturn(item);
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get(ITEM_END_POINT + "/seller/{itemId}", 1L)
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//        }
//
//        @Test
//        @DisplayName("존재하지 않는 상품")
//        public void adminReadTest02() throws Exception {
//            // given
//            given(mockItemQueryService.readById(1L)).willThrow(new EntityNotFoundException());
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get(ITEM_END_POINT + "/seller/{itemId}", 1L)
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
//        }
//    }
//
//    @Nested
//    @DisplayName("판매자 상품 삭제")
//    class AdminRemoveTest {
//        @Test
//        @DisplayName("판매자 상품 삭제 성공")
//        public void adminRemoveTest01() throws Exception {
//            // given
//            given(mockItemQueryService.remove(1L)).willReturn(anyLong());
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    delete(ITEM_END_POINT + "/seller/{itemId}", 1L)
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//        }
//
//        @Test
//        @DisplayName("존재하지 않는 상품 삭제 시도로 인한 삭제 실패")
//        public void adminRemoveTest02() throws Exception {
//            // given
//            given(mockItemQueryService.remove(1L)).willThrow(new EntityNotFoundException());
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    delete(ITEM_END_POINT + "/seller/{itemId}", 1L)
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
//        }
//    }
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
//                    get(ITEM_END_POINT + "/search")
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
//                    get(ITEM_END_POINT + "/search")
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
//                    get(ITEM_END_POINT + "/search")
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
//    @Nested
//    @DisplayName("상품 읽기")
//    class RetrieveOneTest {
//        @Test
//        @DisplayName("상품 읽기 성공")
//        public void RetrieveOneTest01() throws Exception {
//            // given
//            var item = getItemByInfo(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
//            given(mockItemQueryService.readById(1L)).willReturn(item);
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get(ITEM_END_POINT + "/{itemId}", 1L)
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//        }
//
//        @Test
//        @DisplayName("존재하지 않는 상품 조회 시도로 인한 조회 실패")
//        public void RetrieveOneTest02() throws Exception {
//            given(mockItemQueryService.readById(1L)).willThrow(new EntityNotFoundException());
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get(ITEM_END_POINT + "/{itemId}", 1L)
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//            assertTrue(perform.andReturn().getResponse().getContentAsString().contains("\"result\":400"));
//        }
//    }
//
//    @Nested
//    @DisplayName("상품 목록 읽기")
//    class RetrieveAllTest {
//        @Test
//        @DisplayName("전체 상품 목록 읽기 성공")
//        public void retrieveOneTest01() throws Exception {
//            // given
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
//            given(mockItemRelationService.display(pageRequest, null)).willReturn(response);
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get(ITEM_END_POINT)
//                            .param("page", Integer.toString(page))
//                            .param("size", Integer.toString(size))
//                            .accept(MediaType.APPLICATION_JSON));
//
//            // then
//            perform.andExpect(status().isOk());
//        }
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
//    @Nested
//    @DisplayName("전체 상품 개수 읽기")
//    class ReadCountTest {
//        @Test
//        @DisplayName("성공")
//        public void readCountTest01() throws Exception {
//            // given
//            var count = 1L;
//            given(mockItemQueryService.total()).willReturn(count);
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get(ITEM_END_POINT + "/count"));
//
//            // then
//            perform.andExpect(status().isOk());
//            assertTrue(
//                    perform.andReturn()
//                            .getResponse()
//                            .getContentAsString()
//                            .contentEquals(Long.toString(count)
//                            )
//            );
//        }
//    }
//
//    @Nested
//    @DisplayName("검색한 상품 개수 읽기")
//    class SearchCountTest {
//        @Test
//        @DisplayName("성공")
//        public void searchCountTest01() throws Exception {
//            // given
//            var count = 1L;
//            var searchTitle = "title";
//            given(mockItemQueryService.totalByNameContaining(searchTitle)).willReturn(count);
//
//            // when
//            final ResultActions perform = mockMvc.perform(
//                    get(ITEM_END_POINT + "/search/count")
//                            .param("name", searchTitle));
//
//            // then
//            perform.andExpect(status().isOk());
//            assertTrue(
//                    perform.andReturn()
//                            .getResponse()
//                            .getContentAsString()
//                            .contentEquals(Long.toString(count)
//                            )
//            );
//        }
//    }
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
//    private static Item getItemByInfo(List<ItemImage> itemImageList,
//                                         List<OrderItem> orderItemList) {
//        Item item = new Item(
//                1L,
//                "item",
//                10000,
//                1,
//                0,
//                "description",
//                ItemStatus.SELL,
//                new Category(),
//                itemImageList,
//                orderItemList);
//        return item;
//    }
//
//    private ItemRequest.Create makeCreateRequest(Long categoryId) {
//        return new ItemRequest.Create()
//                .setCategoryId(categoryId)
//                .setName("item")
//                .setPrice(10000)
//                .setQuantity(1)
//                .setDescription("description");
//    }
//}