package store.juin.api.controller.seller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.juin.api.domain.RequestUtil;
import store.juin.api.service.command.ItemCommandService;
import store.juin.api.service.query.ItemQueryService;
import store.juin.api.service.query.PrincipalQueryService;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.juin.api.domain.EndPoint.PORT;
import static store.juin.api.domain.EntityUtil.makeAccount;
import static store.juin.api.utils.CharterUtil.DOT;

// https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/#introduction
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class ItemSellerApiControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ItemSellerApiController sut;
    @Mock
    private ItemQueryService itemQueryService;
    @Mock
    private PrincipalQueryService principalQueryService;

    @Mock
    private ItemCommandService itemCommandService;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(sut)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .and().uris().withPort(PORT)         // 포트 설정
                        .and().operationPreprocessors()
                        .withRequestDefaults(prettyPrint())   // request 본문을 예쁘게 출력
                        .withResponseDefaults(prettyPrint())) // response 본문을 예쁘게 출력
                .build();
    }

    @Nested
    @DisplayName("@PostMapping(\"/api/seller/items\")")
    class CreateTest {
        @Test
        @DisplayName("(성공) 관리자가 상품을 등록한다.")
        void createTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var request = makeMultipartFile("request", "request.json", APPLICATION_JSON_VALUE
                    , objectMapper.writeValueAsString(RequestUtil.makeItemCreateRequest()).getBytes()); // FIXME: 한글 입력 시 터짐

            var dir = "src/main/resources/static/image";
            var representativeFile =
                    makeMultipartFile("representativeFile", "cute1.jpg", IMAGE_JPEG_VALUE, new byte[0]);
            var detailFileList = List.of(
                    makeMultipartFile("detailFileList", "cute2.jpg", IMAGE_JPEG_VALUE, new byte[0])
            );

            // when
            final ResultActions actual = mockMvc.perform(multipart("/api/seller/items")
                    .file(request)
                    .file(representativeFile)
                    .file(detailFileList.get(0))
                    .principal(principal)
                    .contentType(MULTIPART_FORM_DATA_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ="));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/seller/items/success/create"
                            , requestParts(
                                    partWithName("request").description("상품 등록 요청 정보")
                                    , partWithName("representativeFile").description("대표 이미지 MultiPartFile")
                                    , partWithName("detailFileList").description("상세 이미지 List<MultiPartFile>")
                            )

                            , requestPartFields("request"
                                    , subsectionWithPath("categoryId").description("아이템이 등록될 카테고리 id")
                                    , subsectionWithPath("name").description("상품명")
                                    , subsectionWithPath("price").description("상품 가격")
                                    , subsectionWithPath("quantity").description("개수")
                                    , subsectionWithPath("description").description("상품 설명")
                            )

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data").type(Long.class).description("등록 상폼 id")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보"))

                    ));
        }
    }

    private MockMultipartFile makeMultipartFile(String fileName, String originalFileName, String contentType, byte[] content) {
        return new MockMultipartFile(fileName, originalFileName, contentType, content);
    }

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
}