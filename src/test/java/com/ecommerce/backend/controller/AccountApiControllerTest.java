package com.ecommerce.backend.controller;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.jwt.JwtTokenUtil;
import com.ecommerce.backend.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountApiControllerTest {
    private static final String ACCOUNT_END_POINT = "/api/accounts";

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @InjectMocks
    private AccountApiController mockAccountApiController;

    @Mock private AccountService mockAccountService;
    @Mock private AuthenticationManager mockAuthenticationManager;
    @Mock private JwtTokenUtil mockJwtTokenUtil;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mockAccountApiController).build();
    }

    @Test
    @DisplayName("signUp - 정상 케이스")
    void signUpCase01() throws Exception {
        // given
        AccountRequest.SignUp request = makeSignUpRequest();

        final Account account = request.toAccount();

        final String json = objectMapper.writeValueAsString(request);

        given(mockAccountService.add(request)).willReturn(account);

        // when
        final ResultActions perform = mockMvc.perform(post(ACCOUNT_END_POINT + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Login - 정상케이스")
    void loginCase01() throws Exception {
        // given
        var request = new AccountRequest.Login();
        request.setEmail("z@z.com");
        request.setPasswordHash("z");

        final String json = objectMapper.writeValueAsString(request);

        final String email = request.getEmail();
        final String password = request.getPasswordHash();
        final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ6QHouY29tIiwiZXhwIjoxNjYxNjg4MDQ4LCJpYXQiOjE2NjE2NzAwNDh9.4hSbtRIphryCzkuEtpsFMcxBTiQId3vU2KB_ljSvQkQoIf_6qS7A9cT07fe6BfqCkaBMLznwq2rnM6Y6vZxXzQ";

        Authentication authentication= new UsernamePasswordAuthenticationToken(email, password);
        given(mockAuthenticationManager.authenticate(authentication)).willReturn(authentication);
        given(mockJwtTokenUtil.generateToken(authentication.getName())).willReturn(token);

        // when
        final ResultActions perform = mockMvc.perform(post(ACCOUNT_END_POINT + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Profile 정상 케이스")
    void profile() throws Exception {
        // Principal mcoking하기
        // ref https://stackoverflow.com/questions/45561471/mock-principal-for-spring-rest-controller

        // given
        final Principal mockPrincipal = mock(Principal.class);

        final String email = "js@mail.com";
        final String password = "$2a$10$JM.sA7K2DQn.JzIYn/GSdeNtcbQmRXw9brm0aGnmohFoEnzwW/M5C";

        var address = makeAddress();
        var account = makeAccount(email, password, address);

        given(mockPrincipal.getName()).willReturn(email);
        given(mockAccountService.readByEmail(mockPrincipal.getName())).willReturn(account);

        // when
        final ResultActions perform = mockMvc.perform(get(ACCOUNT_END_POINT + "/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk());
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
                .registeredAt(LocalDateTime.now())
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
