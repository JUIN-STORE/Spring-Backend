package store.juin.api.controller.user;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.juin.api.domain.enums.AccountRole;
import store.juin.api.service.command.AccountCommandService;
import store.juin.api.service.command.TokenCommandService;
import store.juin.api.service.query.AccountQueryService;
import store.juin.api.service.query.PrincipalQueryService;
import store.juin.api.service.query.TokenQueryService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.juin.api.domain.EndPoint.PORT;
import static store.juin.api.domain.EntityUtil.makeAccount;
import static store.juin.api.domain.EntityUtil.makeToken;
import static store.juin.api.domain.RequestUtil.*;
import static store.juin.api.utils.CharterUtil.DOT;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class AccountApiControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AccountApiController sut;

    @Mock private AuthenticationManager authenticationManager;

    @Mock private TokenQueryService tokenQueryService;
    @Mock private PrincipalQueryService principalQueryService;

    @Mock private TokenCommandService tokenCommandService;
    @Mock private AccountCommandService accountCommandService;
    @Mock private AccountQueryService accountQueryService;

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

            // when
            final ResultActions actual = mockMvc.perform(post("/api/accounts/sign-up")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/accounts/success/signUp"
                            , requestFields(
                                    fieldWithPath("identification").type(String.class).description("아이디")
                                    , fieldWithPath("email").type(String.class).description("이메일")
                                    , fieldWithPath("passwordHash").type(String.class).description("비밀번호")
                                    , fieldWithPath("name").type(String.class).description("이름")
                                    , fieldWithPath("phoneNumber").type(String.class).description("전화번호")
                                    , fieldWithPath("accountRole").type(AccountRole.class).description("권한")
                                    , fieldWithPath("address.city").type(String.class).description("도시")
                                    , fieldWithPath("address.street").type(String.class).description("상세 주소")
                                    , fieldWithPath("address.zipCode").type(Integer.class).description("우편번호")
                                    , fieldWithPath("address.defaultAddress").type(boolean.class).description("기본 주소 여부")
                            )

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data.identification").type(String.class).description("아이디")
                                    , fieldWithPath("data.email").type(String.class).description("이메일")
                                    , fieldWithPath("data.name").type(String.class).description("이름")
                                    , fieldWithPath("data.accountRole").type(AccountRole.class).description("권한")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보"))
                    ));
        }
    }

    @Nested
    @DisplayName("POST /api/accounts/login")
    class LoginTest {
        @Test
        @DisplayName("(성공) 로그인을 한다.")
        void loginTest01() throws Exception {
            // given
            var identification = "z@z.com";
            var password = "z";
            var request = makeLoginRequest(identification, password);

            var accessToken = "this_is_a_accessToken";
            var refreshToken = "this_is_a_refreshToken";

            var authentication = new UsernamePasswordAuthenticationToken(identification, password);
            given(authenticationManager.authenticate(authentication)).willReturn(authentication);
            given(tokenCommandService.addAccessToken(identification)).willReturn(accessToken);
            given(tokenCommandService.upsertRefreshToken(identification)).willReturn(refreshToken);

            // when
            final ResultActions actual = mockMvc.perform(post("/api/accounts/login")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/accounts/success/login"
                                    , requestFields(
                                            fieldWithPath("identification").type(String.class).description("가입 아이디")
                                            , fieldWithPath("passwordHash").type(String.class).description("비밀번호")
                                    )

                                    , responseFields(
                                        fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                        , fieldWithPath("data.identification").type(String.class).description("아이디")
                                        , fieldWithPath("data.token.accessToken").type(String.class).description("엑세스 토큰")

                                        , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                        , fieldWithPath("region").type(String.class).description("리전 정보"))
                            ));
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
            final ResultActions actual = mockMvc.perform(get("/api/accounts/logout")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/accounts/success/logout"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data").type(String.class).description("로그아웃 성공 여부")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보"))
                    ));
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
            final ResultActions actual = mockMvc.perform(get("/api/accounts/profile")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/accounts/success/profile"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data.id").type(Long.class).description("id")
                                    , fieldWithPath("data.identification").type(String.class).description("아이디")
                                    , fieldWithPath("data.email").type(String.class).description("이메일")
                                    , fieldWithPath("data.name").type(String.class).description("이름")
                                    , fieldWithPath("data.phoneNumber").type(String.class).description("전화번호")
                                    , fieldWithPath("data.accountRole").type(AccountRole.class).description("권한")
                                    , fieldWithPath("data.address.id").type(Long.class).description("주소 id")
                                    , fieldWithPath("data.address.city").type(String.class).description("도시")
                                    , fieldWithPath("data.address.street").type(String.class).description("도로명")
                                    , fieldWithPath("data.address.zipCode").type(Integer.class).description("우편번호")
                                    , fieldWithPath("data.address.defaultAddress").type(Boolean.class).description("기본 주소 여부")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보"))
                    ));
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
            var request = makeAccountUpdateRequest(newPassword, null, null, null);
            var updateAccount = makeAccount(newPassword);
            given(accountCommandService.modify(account, request)).willReturn(updateAccount);

            // when
            final ResultActions actual = mockMvc.perform(patch("/api/accounts")
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/accounts/success/update"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , requestFields(
                                    fieldWithPath("passwordHash").type(String.class).description("새로운 비밀번호")
                                    , fieldWithPath("name").type(String.class).description("새로운 이름")
                                    , fieldWithPath("phoneNumber").type(String.class).description("새로운 전화번호")
                                    , fieldWithPath("accountRole").type(AccountRole.class).description("새로운 권한")
                            )
                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data.email").type(String.class).description("이메일")
                                    , fieldWithPath("data.name").type(String.class).description("이름")
                                    , fieldWithPath("data.phoneNumber").type(String.class).description("전화번호")
                                    , fieldWithPath("data.accountRole").type(AccountRole.class).description("권한")
                                    , fieldWithPath("data.updatedAt").type(LocalDateTime.class).description("수정 시각")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보"))
                    ));
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
            final ResultActions actual = mockMvc.perform(delete("/api/accounts/{accountId}", accountId)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/accounts/success/delete"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , pathParameters(
                                    parameterWithName("accountId").description("계정 id")
                            )

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data.id").type(String.class).description("계정 id")
                                    , fieldWithPath("data.identification").type(String.class).description("계정 아이디")
                                    , fieldWithPath("data.email").type(String.class).description("이메일")
                                    , fieldWithPath("data.name").type(String.class).description("이름")
                                    , fieldWithPath("data.accountRole").type(AccountRole.class).description("권한")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보"))
                    ));
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
            final ResultActions actual = mockMvc.perform(get("/api/accounts/duplication/{identification}", identification)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + "/user/accounts/success/check-identification"
                            , pathParameters(
                                    parameterWithName("identification").description("가입하려는 아이디")
                            )

                            , responseFields(
                                    fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")

                                    , fieldWithPath("data").type(Void.class).description("데이터")

                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("리전 정보"))
                    ));

        }
    }
}
