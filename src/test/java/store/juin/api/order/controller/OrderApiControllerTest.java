package store.juin.api.order.controller;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import store.juin.api.order.enumeration.OrderStatus;
import store.juin.api.order.model.request.OrderCancelRequest;
import store.juin.api.order.service.command.OrderCommandService;
import store.juin.api.order.service.query.OrderQueryService;
import store.juin.api.principal.service.query.PrincipalQueryService;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.juin.api.common.EndPoint.PORT;
import static store.juin.api.common.EntityUtil.makeAccount;
import static store.juin.api.common.RequestUtil.makeOrderCreateRequest;
import static store.juin.api.common.RequestUtil.makeOrderRetrieveRequest;
import static store.juin.api.common.ResponseUtil.makeOrderJoinResponse;
import static store.juin.api.util.CharterUtil.DOT;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class OrderApiControllerTest {
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private OrderApiController sut;
    @Mock
    private OrderQueryService orderQueryService;
    @Mock
    private PrincipalQueryService principalQueryService;

    @Mock
    private OrderCommandService orderCommandService;

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
    @DisplayName("POST /api/orders")
    class CreateTest {
        @Test
        @DisplayName("(성공) 주문을 한다.")
        void createTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount("asdq13@#13$");
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var request = makeOrderCreateRequest();
            given(orderCommandService.add(account, request)).willReturn(3L);

            // when
            final ResultActions actual = mockMvc.perform(post("/api/orders")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/orders/success/create"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , requestFields(
                                    fieldWithPath("itemIdList").type(ArrayList.class).description("상품 아이디 리스트")
                                    , fieldWithPath("count").type(int.class).description("주문한 상품 총 개수")
                                    , fieldWithPath("grandTotal").type(int.class).description("주문한 상품 총 가격")
                                    , fieldWithPath("orderStatus").type(OrderStatus.class).description("주문 상태")
                                    , fieldWithPath("deliveryReceiver.receiverName").type(String.class).description("주문자")
                                    , fieldWithPath("deliveryReceiver.receiverPhoneNumber").type(String.class).description("주문자 전화번호")
                                    , fieldWithPath("deliveryReceiver.receiverEmail").type(String.class).description("주문자 이메일")
                                    , fieldWithPath("deliveryAddress.city").type(String.class).description("주문자 도시")
                                    , fieldWithPath("deliveryAddress.street").type(String.class).description("주문자 상세 주소")
                                    , fieldWithPath("deliveryAddress.zipCode").type(Integer.class).description("주문자 우편번호")
                                    , fieldWithPath("deliveryAddress.defaultAddress").type(boolean.class).description("주문자 기본 배송지 여부")
                            )

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data").type(Long.class).description("주문 id")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("라전 정보"))

                    ));
        }
    }

    @Nested
    @DisplayName("GET /api/orders")
    class RetrieveAllTest {
        @Test
        @DisplayName("(성공) 주문 상세 내역을 조회한다.")
        void retrieveAllTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount("asdq13@#13$");
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var page = 0;
            var size = 10;
            var request = makeOrderRetrieveRequest();
            var pageable = PageRequest.of(page, size);
            var joinResponse = makeOrderJoinResponse();

            var response = new PageImpl<>(List.of(joinResponse), pageable, List.of(joinResponse).size());
            given(orderQueryService.readAll(account, request, pageable)).willReturn(response);

            // when
            final ResultActions actual = mockMvc.perform(get("/api/orders")
                            .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                            .principal(principal)
                            .param("startDate", request.getStartDate().toString())
                            .param("endDate", request.getEndDate().toString())
                            .param("orderStatus", OrderStatus.ORDER.name())
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/orders/success/retrieveAll"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))
                            
                            , requestParameters(
                                    parameterWithName("startDate").description("어디서부터 조회할지 시작 날짜")
                                    , parameterWithName("endDate").description("어디까지 조회할지 끝나는 날짜")
                                    , parameterWithName("orderStatus").description("주문 상태")
                                    , parameterWithName("page").description("페이지 번호")
                                    , parameterWithName("size").description("몇 개의 페이지를 가져올 것인지")
                            )

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data.content[].itemImageName").type(Long.class).description("copy-이미지 이름")
                                    , fieldWithPath("data.content[].originImageName").type(Long.class).description("이미지 이름")
                                    , fieldWithPath("data.content[].imageUrl").type(Long.class).description("이미지 url")
                                    , fieldWithPath("data.content[].ordersId").type(Long.class).description("주문 id")
                                    , fieldWithPath("data.content[].orderItemId").type(Long.class).description("주문 상품 id")
                                    , fieldWithPath("data.content[].itemId").type(Long.class).description("상품 id")
                                    , fieldWithPath("data.content[].deliveryId").type(Long.class).description("배송 id")
                                    , fieldWithPath("data.content[].orderCount").type(int.class).description("주문한 상품 개수")
                                    , fieldWithPath("data.content[].price").type(int.class).description("주문한 상품 가격")
                                    , fieldWithPath("data.content[].name").type(String.class).description("주문한 상품 이름")
                                    , fieldWithPath("data.content[].orderDate").type(ZonedDateTime.class).description("주문한 시각")
                                    , fieldWithPath("data.content[].orderStatus").type(OrderStatus.class).description("주문 상태")

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

    @Nested
    @DisplayName("POST /api/orders/cancel")
    class CancelTest {
        @Test
        @DisplayName("(성공) 주문을 취소한다")
        void cancelTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount("asdq13@#13$");
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var orderId = 3L;
            var request = new OrderCancelRequest().setOrderId(orderId);
            given(orderCommandService.cancel(orderId, account.getId())).willReturn(orderId);

            // when
            final ResultActions actual = mockMvc.perform(post("/api/orders/cancel")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/orders/success/cancel"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , requestFields(
                                    fieldWithPath("orderId").type(Long.class).description("주문 취소할 주문 id")
                            )

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data").type(Long.class).description("주문 id")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("라전 정보"))
                    ));
        }
    }
}