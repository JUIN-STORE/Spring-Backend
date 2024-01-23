package store.juin.api.account.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.service.command.AccountCommandService;
import store.juin.api.account.service.query.AccountQueryService;
import store.juin.api.principal.service.query.PrincipalQueryService;
import store.juin.api.token.service.TokenCommandService;
import store.juin.api.token.service.TokenQueryService;

import java.security.Principal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.juin.api.common.EndPoint.PORT;
import static store.juin.api.common.EntityUtil.makeAccount;
import static store.juin.api.common.EntityUtil.makeToken;
import static store.juin.api.common.RequestUtil.*;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class AccountApiControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AccountApiController sut;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenQueryService tokenQueryService;
    @Mock
    private PrincipalQueryService principalQueryService;

    @Mock
    private TokenCommandService tokenCommandService;
    @Mock
    private AccountCommandService accountCommandService;
    @Mock
    private AccountQueryService accountQueryService;

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(sut)
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .and().uris().withPort(PORT)          // 포트 설정
                        .and().operationPreprocessors()
                        .withRequestDefaults(prettyPrint())   // request 본문을 예쁘게 출력
                        .withResponseDefaults(prettyPrint())) // response 본문을 예쁘게 출력
                .build();
    }

    @Nested
    @DisplayName("POST /api/accounts/sign-up")
    class SignUpTest {
        @Test
        @DisplayName("(정상) 회원가입을 한다.")
        void signUpTest01() throws Exception {
            // given
            var request = makeSignUpRequest();
            var account = makeAccount("asdq13@#13$");

            given(accountCommandService.add(request)).willReturn(account);
            given(accountCommandService.isConfirmed(request.getEmail())).willReturn(true);

            // when
            final ResultActions actual = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/accounts/sign-up")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)));

            // then
            actual.andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper
                        .document("회원 가입", "회원 가입을 한다.", "회원 가입"
                            , PayloadDocumentation.requestFields(
                                  PayloadDocumentation.fieldWithPath("identification").type(JsonFieldType.STRING).description("아이디")
                                , PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                , PayloadDocumentation.fieldWithPath("passwordHash").type(JsonFieldType.STRING).description("비밀번호")
                                , PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                                , PayloadDocumentation.fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                                , PayloadDocumentation.fieldWithPath("accountRole").type(JsonFieldType.STRING).description("권한")
                                , PayloadDocumentation.fieldWithPath("address.city").type(JsonFieldType.STRING).description("도시")
                                , PayloadDocumentation.fieldWithPath("address.street").type(JsonFieldType.STRING).description("상세 주소")
                                , PayloadDocumentation.fieldWithPath("address.zipCode").type(JsonFieldType.NUMBER).description("우편번호")
                                , PayloadDocumentation.fieldWithPath("address.defaultAddress").type(JsonFieldType.BOOLEAN).description("기본 주소 여부")
                            ),

                            PayloadDocumentation.responseFields(
                                  PayloadDocumentation.fieldWithPath("apiStatus").type(JsonFieldType.NUMBER).description("api 요청에 대한 상태")

                                , PayloadDocumentation.fieldWithPath("data.identification").type(JsonFieldType.STRING).description("아이디")
                                , PayloadDocumentation.fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일")
                                , PayloadDocumentation.fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름")
                                , PayloadDocumentation.fieldWithPath("data.accountRole").type(JsonFieldType.STRING).description("권한")

                                , PayloadDocumentation.fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("API 요청 시각")
                                , PayloadDocumentation.fieldWithPath("region").type(JsonFieldType.STRING).description("리전 정보")
                            )
                        )
                    );
        }
    }


    @Nested
    @DisplayName("POST /api/accounts/sign-in")
    class SignInTest {
        @Test
        @DisplayName("(성공) 로그인을 한다.")
        void loginTest01() throws Exception {
            // given
            var identification = "z@z.com";
            var password = "z";
            var request = makeSignInRequest(identification, password);

            var accessToken = "this_is_a_accessToken";
            var refreshToken = "this_is_a_refreshToken";

            var authentication = new UsernamePasswordAuthenticationToken(identification, password);
            given(authenticationManager.authenticate(authentication)).willReturn(authentication);
            given(tokenCommandService.addAccessToken(identification)).willReturn(accessToken);
            given(tokenCommandService.upsertRefreshToken(identification)).willReturn(refreshToken);

            // when
            final ResultActions actual = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/accounts/sign-in")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)));

            // then
            actual.andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper
                        .document("로그인", "로그인하기", "유저 로그인"
                            , PayloadDocumentation.requestFields(
                                  PayloadDocumentation.fieldWithPath("identification").type(JsonFieldType.STRING).description("가입 아이디")
                                , PayloadDocumentation.fieldWithPath("passwordHash").type(JsonFieldType.STRING).description("비밀번호")
                            ),

                            PayloadDocumentation.responseFields(
                                      PayloadDocumentation.fieldWithPath("apiStatus").type(JsonFieldType.NUMBER).description("api 요청에 대한 상태")

                                    , PayloadDocumentation.fieldWithPath("data.identification").type(JsonFieldType.STRING).description("아이디")
                                    , PayloadDocumentation.fieldWithPath("data.token.accessToken").type(JsonFieldType.STRING).description("엑세스 토큰")

                                    , PayloadDocumentation.fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("API 요청 시각")
                                    , PayloadDocumentation.fieldWithPath("region").type(JsonFieldType.STRING).description("리전 정보")
                            )
                        )
                    );
        }
    }

    @Nested
    @DisplayName("GET /api/accounts/logout")
    class LogoutTest {
        @Test
        @DisplayName("(성공) 로그아웃을 한다.")
        void logoutTest01() throws Exception {
            // given
            String identification = "junsu0325";

            var principal = mock(Principal.class);
            given(principal.getName()).willReturn(identification);
            given(tokenQueryService.readByIdentification(identification)).willReturn(makeToken());

            // when
            final ResultActions actual = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/accounts/logout")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual.andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper
                        .document("로그아웃", "로그아웃하기", "유저 로그아웃"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("apiStatus").type(JsonFieldType.NUMBER).description("api 요청에 대한 상태")

                                , PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.STRING).description("로그아웃 성공 여부")

                                , PayloadDocumentation.fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("API 요청 시각")
                                , PayloadDocumentation.fieldWithPath("region").type(JsonFieldType.STRING).description("리전 정보")
                            )
                        )
                    );
        }
    }

    @Nested
    @DisplayName("GET /api/accounts/profile")
    class ProfileTest {
        @Test
        @DisplayName("(정상) 내 정보를 읽어온다.")
        void profileTest01() throws Exception {
            // Principal mcoking하기, https://stackoverflow.com/questions/45561471/mock-principal-for-spring-rest-controller
            // given
            var principal = mock(Principal.class);
            var account = makeAccount("asdq13@#13$");
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            // when
            final ResultActions actual = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/accounts/profile")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual.andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper
                        .document("내 정보 읽기", "내 정보를 읽어온다.", "내 정보 읽기"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("apiStatus").type(JsonFieldType.NUMBER).description("api 요청에 대한 상태")

                                , PayloadDocumentation.fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("id")
                                , PayloadDocumentation.fieldWithPath("data.identification").type(JsonFieldType.STRING).description("아이디")
                                , PayloadDocumentation.fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일")
                                , PayloadDocumentation.fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름")
                                , PayloadDocumentation.fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                                , PayloadDocumentation.fieldWithPath("data.accountRole").type(JsonFieldType.STRING).description("권한")
                                , PayloadDocumentation.fieldWithPath("data.address.id").type(JsonFieldType.NUMBER).description("주소 id")
                                , PayloadDocumentation.fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("도시")
                                , PayloadDocumentation.fieldWithPath("data.address.street").type(JsonFieldType.STRING).description("도로명")
                                , PayloadDocumentation.fieldWithPath("data.address.zipCode").type(JsonFieldType.NUMBER).description("우편번호")
                                , PayloadDocumentation.fieldWithPath("data.address.defaultAddress").type(JsonFieldType.BOOLEAN).description("기본 주소 여부")

                                , PayloadDocumentation.fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("API 요청 시각")
                                , PayloadDocumentation.fieldWithPath("region").type(JsonFieldType.STRING).description("리전 정보")
                            )
                        )
                    );
        }
    }

    @Nested
    @DisplayName("PATCH /api/accounts")
    class UpdateTest {
        @Test
        @DisplayName("(정상) 회원 정보를 수정한다.")
        void updateTest01() throws Exception {
            // Principal mcoking하기, https://stackoverflow.com/questions/45561471/mock-principal-for-spring-rest-controller
            // given
            var principal = mock(Principal.class);
            var account = makeAccount("asdq13@#13$");
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var newPassword = "new_password";
            var request = makeAccountUpdateRequest(newPassword, "junsu", "010-1234-5678", AccountRole.USER);
            var updateAccount = makeAccount(newPassword);
            given(accountCommandService.modify(account, request)).willReturn(updateAccount);

            // when
            final ResultActions actual = mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/accounts")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            actual.andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper
                        .document("회원 정보 수정", "회원 정보를 수정한다.", "회원 정보 수정"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , PayloadDocumentation.requestFields(
                                  PayloadDocumentation.fieldWithPath("passwordHash").type(JsonFieldType.STRING).description("새로운 비밀번호")
                                , PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING).description("새로운 이름")
                                , PayloadDocumentation.fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("새로운 전화번호")
                                , PayloadDocumentation.fieldWithPath("accountRole").type(JsonFieldType.STRING).description("새로운 권한")
                            )

                            , PayloadDocumentation.responseFields(
                                  PayloadDocumentation.fieldWithPath("apiStatus").type(JsonFieldType.NUMBER).description("api 요청에 대한 상태")

                                , PayloadDocumentation.fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일")
                                , PayloadDocumentation.fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름")
                                , PayloadDocumentation.fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                                , PayloadDocumentation.fieldWithPath("data.accountRole").type(JsonFieldType.STRING).description("권한")
                                , PayloadDocumentation.fieldWithPath("data.updatedAt").type(JsonFieldType.ARRAY).description("수정 시각")

                                , PayloadDocumentation.fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("API 요청 시각")
                                , PayloadDocumentation.fieldWithPath("region").type(JsonFieldType.STRING).description("리전 정보")
                            )
                        )
                    );
        }
    }

    @Nested
    @DisplayName("DELETE /api/accounts/{accountId}")
    class DeleteTest {
        @Test
        @DisplayName("(정상) 회원 정보를 삭제한다.")
        void deleteTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount("asdq13@#13$");
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var accountId = 22L;
            given(accountCommandService.remove(account, accountId)).willReturn(account);

            // when
            final ResultActions actual = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/accounts/{accountId}", accountId)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual.andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper
                        .document("회원 정보 삭제", "회원 정보를 삭제한다.", "회원 정보 삭제"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))
                            , pathParameters(parameterWithName("accountId").description("계정 id"))

                            , PayloadDocumentation.responseFields(
                                  PayloadDocumentation.fieldWithPath("apiStatus").type(JsonFieldType.NUMBER).description("api 요청에 대한 상태")

                                , PayloadDocumentation.fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("계정 id")
                                , PayloadDocumentation.fieldWithPath("data.identification").type(JsonFieldType.STRING).description("계정 아이디")
                                , PayloadDocumentation.fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일")
                                , PayloadDocumentation.fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름")
                                , PayloadDocumentation.fieldWithPath("data.accountRole").type(JsonFieldType.STRING).description("권한")

                                , PayloadDocumentation.fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("API 요청 시각")
                                , PayloadDocumentation.fieldWithPath("region").type(JsonFieldType.STRING).description("리전 정보")
                            )
                        )
                    );
        }
    }

    @Nested
    @DisplayName("GET /api/accounts/duplication/{identification}")
    class CheckIdentificationTest {
        @Test
        @DisplayName("(정상) 아이디 중복을 체크한다.")
        void checkIdentificationTest01() throws Exception {
            // given
            var identification = "junsu0325@naver.com";

            // when
            final ResultActions actual = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/accounts/duplication/{identification}", identification)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

            // then
            actual.andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper
                        .document("아이디 중복 체크", "아이디 중복을 체크한다.", "아이디 중복 체크"
                            , pathParameters(parameterWithName("identification").description("가입하려는 아이디"))

                            , PayloadDocumentation.responseFields(
                                  PayloadDocumentation.fieldWithPath("apiStatus").type(JsonFieldType.NUMBER).description("api 요청에 대한 상태")

                                , PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")

                                , PayloadDocumentation.fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("API 요청 시각")
                                , PayloadDocumentation.fieldWithPath("region").type(JsonFieldType.STRING).description("리전 정보")
                            )
                        )
                    );
        }
    }
}
