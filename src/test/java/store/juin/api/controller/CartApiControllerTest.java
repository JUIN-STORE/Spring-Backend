package store.juin.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.juin.api.service.command.CartItemCommandService;
import store.juin.api.service.query.CartQueryService;
import store.juin.api.service.query.PrincipalQueryService;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.juin.api.controller.EndPoint.*;
import static store.juin.api.domain.EntityUtil.makeAccount;
import static store.juin.api.domain.RequestUtil.makeCartItemAddRequest;
import static store.juin.api.domain.RequestUtil.makeCartItemUpdateRequest;
import static store.juin.api.domain.ResponseUtil.makeCartItemBuyResponseList;
import static store.juin.api.domain.ResponseUtil.makeCartItemRetrieveResponseList;
import static store.juin.api.utils.CharterUtil.DOT;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class CartApiControllerTest {
    private static final String ADD = API_CARTS + "/add";
    private static final String BUY = API_CARTS + "/buy";
    private static final String QUANTITY = API_CARTS + "/quantity";
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CartApiController sut;
    @Mock
    private CartQueryService cartQueryService;
    @Mock
    private PrincipalQueryService principalQueryService;

    @Mock
    private CartItemCommandService cartItemCommandService;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(sut)
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .and().uris().withPort(PORT)         // 포트 설정
                        .and().operationPreprocessors()
                        .withRequestDefaults(prettyPrint())   // request 본문을 예쁘게 출력
                        .withResponseDefaults(prettyPrint())) // response 본문을 예쁘게 출력
                .build();
    }

    @Nested
    @DisplayName(POST + ADD)
    class CreateTest {
        @Test
        @DisplayName("카트에 항목을 추가한다.")
        void createTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var request = makeCartItemAddRequest();

            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var transaction = 1;
            given(cartItemCommandService.add(account, request)).willReturn(transaction);

            // when
            final ResultActions actual = mockMvc.perform(post(ADD)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal)
                    .content(objectMapper.writeValueAsBytes(request)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + API_CARTS + "/create"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , requestFields(fieldWithPath("itemId").type(Long.class).description("item id")
                                    , fieldWithPath("count").type(Integer.class).description("몇 개를 추가할 것인지?")
                            )
                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    // data
                                    , fieldWithPath("data").type(int.class).description("몇 개의 항목이 추가되었는지")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보")

                            )
                    ));
        }
    }

    @Nested
    @DisplayName(GET + API_CARTS)
    class RetrieveOneTest {
        @Test
        @DisplayName("카트에 있는 제품 정보를 읽어온다.")
        void retrieveOneTest() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            given(cartQueryService.makeCartItemRetrieveResponseList(account)).willReturn(makeCartItemRetrieveResponseList());

            // when
            final ResultActions actual = mockMvc.perform(get(API_CARTS)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + API_CARTS + "/retrieveOne"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    // data
                                    , fieldWithPath("data[].itemId").type(Long.class).description("item id")
                                    , fieldWithPath("data[].count").type(Integer.class).description("카트 안에 들어 있는 제품의 총 개수")
                                    , fieldWithPath("data[].itemName").type(String.class).description("상세 주소")
                                    , fieldWithPath("data[].price").type(Integer.class).description("우편 번호")
                                    , fieldWithPath("data[].description").type(String.class).description("기본 주소")
                                    , fieldWithPath("data[].itemImageName").type(String.class).description("원본 이미지 파일명을 통해 만든 파일명")
                                    , fieldWithPath("data[].originImageName").type(String.class).description("원본 이미지 파일명")
                                    , fieldWithPath("data[].imageUrl").type(String.class).description("이미지 조회 경로")
                                    , fieldWithPath("data[].thumbnail").type(Boolean.class).description("썸네일 여부")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName(GET + BUY)
    class BuyTest {
        @Test
        @DisplayName("카트에서 buy를 클릭하면 주문할 정보 데이터를 읽어온다.")
        void buyTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var itemIdList = List.of(1L, 2L);

            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            given(cartQueryService.makeCartItemBuyResponseList(account, itemIdList))
                    .willReturn(makeCartItemBuyResponseList());

            // when
            final ResultActions actual = mockMvc.perform(get(BUY)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal)
                    .param("itemIdList", itemIdList.stream().map(String::valueOf).toArray(String[]::new)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + BUY
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , requestParameters(
                                    parameterWithName("itemIdList").description("item id list")
                            )

                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    // data
                                    , fieldWithPath("data[].count").type(Integer.class).description("제품의 총 개수")
                                    , fieldWithPath("data[].item.itemId").type(Long.class).description("item id")
                                    , fieldWithPath("data[].item.itemName").type(String.class).description("상품 이름")
                                    , fieldWithPath("data[].item.price").type(Integer.class).description("상품 가격")
                                    , fieldWithPath("data[].item.description").type(Integer.class).description("상품 설명")
                                    , fieldWithPath("data[].itemImage.name").type(String.class).description("이미지 사본 파일명")
                                    , fieldWithPath("data[].itemImage.originName").type(String.class).description("이미지 원본 파일명")
                                    , fieldWithPath("data[].itemImage.imageUrl").type(String.class).description("이미지 저장 경로")
                                    , fieldWithPath("data[].itemImage.thumbnail").type(Boolean.class).description("썸네일 여부")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName(PUT + QUANTITY)
    class UpdateQuantityTest {
        @Test
        @DisplayName("카트에 담긴 상품 개수 변경한다.")
        void updateQuantityTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var request = makeCartItemUpdateRequest();
            given(cartItemCommandService.modifyQuantity(account, request)).willReturn(1);

            // when
            final ResultActions actual = mockMvc.perform(put(QUANTITY)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + QUANTITY
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    // data
                                    , fieldWithPath("data").type(Integer.class).description("변경된 수량 개수")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보")

                            )
                    ));
        }
    }

    @Nested
    @DisplayName(DELETE + API_CARTS)
    class DeleteTest {
        @Test
        @DisplayName("카트에 추가된 상품을 제거한다.")
        void deleteTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var itemIdList = List.of(1L, 2L);

            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            given(cartItemCommandService.remove(account, itemIdList)).willReturn(2L);

            // when
            final ResultActions actual = mockMvc.perform(delete(API_CARTS)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal)
                    .param("itemIdList", itemIdList.stream().map(String::valueOf).toArray(String[]::new)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + API_CARTS + "/delete"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , requestParameters(
                                    parameterWithName("itemIdList").description("item id list")
                            )

                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    // data
                                    , fieldWithPath("data").type(Long.class).description("카트에서 삭제된 상품 개수")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보")

                            )
                    ));
        }
    }
}