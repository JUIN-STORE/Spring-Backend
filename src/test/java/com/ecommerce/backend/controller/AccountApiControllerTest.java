package com.ecommerce.backend.controller;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
    @Mock private JwtTokenUtil mockkJwtTokenUtil;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mockAccountApiController).build();
    }

    @Test
    @DisplayName("정상적으로 회원가입에 성공")
    void signUpCase01() throws Exception {
        // given
        var signUp = new AccountRequest.SignUp();
        signUp.setEmail("js@gmail.com");
        signUp.setPasswordHash("asdq13@#13$");
        signUp.setName("준수");
        signUp.setPhoneNumber("010-1111-2222");
        signUp.setAccountRole(AccountRole.ADMIN);

        var registerRequest = new AddressRequest.Register();
        registerRequest.setCity("도시");
        registerRequest.setStreet("상세 주소");
        registerRequest.setZipCode(12345);

        signUp.setAddress(registerRequest);

        final String json = objectMapper.writeValueAsString(signUp);

        // when
        final ResultActions perform = mockMvc.perform(post(ACCOUNT_END_POINT + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json));

        // then
        perform.andExpect(status().isOk());
    }
}
