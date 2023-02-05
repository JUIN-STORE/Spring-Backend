package store.juin.api.controller;

import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Address;
import store.juin.api.domain.enums.AccountRole;
import store.juin.api.domain.request.AccountRequest;
import store.juin.api.domain.request.AddressRequest;
import store.juin.api.service.command.AccountCommandService;
import store.juin.api.service.command.TokenCommandService;
import store.juin.api.service.query.PrincipalQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class AccountApiControllerTest {
    private static final String ACCOUNT_END_POINT = "/api/accounts";
    private static final String LOGIN_END_POINT = ACCOUNT_END_POINT + "/login";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @InjectMocks
    private AccountApiController sut;

    @Mock private AuthenticationManager authenticationManager;

    @Mock private PrincipalQueryService principalQueryService;

    @Mock private AccountCommandService accountCommandService;

    @Mock private TokenCommandService tokenCommandService;


    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(sut)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @Test
    @DisplayName("signUp - 정상 케이스")
    void signUpCase01() throws Exception {
        // given
        var request = makeSignUpRequest();
        var account = request.toAccount();
        var json = objectMapper.writeValueAsString(request);

        given(accountCommandService.add(request)).willReturn(account);

        // when
        final ResultActions actual = mockMvc.perform(post(ACCOUNT_END_POINT + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json));

        // then
        actual
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Login - 정상케이스")
    void loginCase01() throws Exception {
        // given
        var email = "z@z.com";
        var password = "z";
        var request = new AccountRequest.Login()
                .setEmail(email)
                .setPasswordHash(password);

        var json = objectMapper.writeValueAsString(request);

        var accessToken = "this is a accessToken";
        var refreshToken = "this is a refreshToken";

        var authentication= new UsernamePasswordAuthenticationToken(email, password);
        given(authenticationManager.authenticate(authentication)).willReturn(authentication);
        given(tokenCommandService.addAccessToken(email)).willReturn(accessToken);

        // when
        final ResultActions actual = mockMvc.perform(post(LOGIN_END_POINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json));

        // then
        actual
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Profile 정상 케이스")
    void profile() throws Exception {
        // Principal mcoking하기, https://stackoverflow.com/questions/45561471/mock-principal-for-spring-rest-controller
        // given
        var mockPrincipal = mock(Principal.class);

        var email = "js@mail.com";
        var password = "$2a$10$JM.sA7K2DQn.JzIYn/GSdeNtcbQmRXw9brm0aGnmohFoEnzwW/M5C";
        var account = makeAccount(email, password, makeAddress());

        given(mockPrincipal.getName()).willReturn(email);
        given(principalQueryService.readByPrincipal(mockPrincipal)).willReturn(account);

        // when
        final ResultActions actual = mockMvc.perform(get(ACCOUNT_END_POINT + "/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON));

        // then
        actual
                .andExpect(status().isOk());
    }

    private Address makeAddress() {
        return Address.builder()
                .city("서울시")
                .street("동작구")
                .zipCode(11111)
                .build();
    }

    private Account makeAccount(String email, String password, Address address) {
        return Account.builder()
                .id(9L)
                .email(email)
                .passwordHash(password)
                .name("js")
                .phoneNumber("01012345678")
                .accountRole(AccountRole.ADMIN)
                .lastLogin(LocalDateTime.now())
                .addressList(List.of(address))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private AccountRequest.SignUp makeSignUpRequest() {
        var request = new AccountRequest.SignUp();
        request.setEmail("js@gmail.com");
        request.setPasswordHash("asdq13@#13$");
        request.setName("준수");
        request.setPhoneNumber("010-1111-2222");
        request.setAccountRole(AccountRole.ADMIN);

        var addressRequest = new AddressRequest.Register();
        addressRequest.setCity("도시");
        addressRequest.setStreet("상세 주소");
        addressRequest.setZipCode(12345);

        request.setAddress(addressRequest);
        return request;
    }
}
