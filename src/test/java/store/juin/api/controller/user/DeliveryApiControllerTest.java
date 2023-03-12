package store.juin.api.controller.user;

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
import store.juin.api.domain.enums.DeliveryStatus;
import store.juin.api.service.query.DeliveryQueryService;
import store.juin.api.service.query.PrincipalQueryService;

import java.security.Principal;
import java.time.ZonedDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.juin.api.domain.EndPoint.PORT;
import static store.juin.api.domain.EntityUtil.makeAccount;
import static store.juin.api.domain.EntityUtil.makeDelivery;
import static store.juin.api.utils.CharterUtil.DOT;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class DeliveryApiControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private DeliveryApiController sut;

    @Mock private DeliveryQueryService deliveryQueryService;
    @Mock private PrincipalQueryService principalQueryService;

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
    @DisplayName("GET /api/deliveries/{deliveryId}")
    class RetrieveOneTest {
        @Test
        @DisplayName("(성공) 주문에 대한 배송 상세 내용을 조회한다.")
        void retrieveOneTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount("asdq13@#13$");
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var deliveryId = 31L;
            var delivery = makeDelivery(deliveryId);
            given(deliveryQueryService.readById(deliveryId, account.getId())).willReturn(delivery);

            // when
            final ResultActions actual = mockMvc.perform(get("/api/deliveries/{deliveryId}", deliveryId)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/deliveries/success/retrieveOne"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , pathParameters(
                                    parameterWithName("deliveryId").description("배송 id")
                            )

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data.deliveryId").type(Long.class).description("배송 id")
                                    , fieldWithPath("data.receiverEmail").type(String.class).description("받는 사람 이메일")
                                    , fieldWithPath("data.receiverName").type(String.class).description("받는 사람 이름")
                                    , fieldWithPath("data.receiverPhoneNumber").type(String.class).description("받는 사람 전화번호")
                                    , fieldWithPath("data.deliveryStatus").type(DeliveryStatus.class).description("배송 상태")
                                    , fieldWithPath("data.addressId").type(Long.class).description("배송지 id")
                                    , fieldWithPath("data.city").type(String.class).description("배송지 도시")
                                    , fieldWithPath("data.street").type(String.class).description("배송지 상세 주소")
                                    , fieldWithPath("data.zipCode").type(Integer.class).description("배송지 우편번호")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("라전 정보"))

                    ));
        }
    }
}