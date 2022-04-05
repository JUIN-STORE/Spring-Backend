package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.domain.response.AccountResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityExistsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AccountServiceTests {
    @Autowired
    private AccountService accountService;

    public Account createAccount(AccountRequest.RegisterRequest request){
        return request.toAccount();
    }

    public AccountRequest.RegisterRequest createAccountRequest() {
        return new AccountRequest.RegisterRequest()
                .setEmail("test@test.com")
                .setName("릴러말즈")
                .setPasswordHash("test")
                .setAccountRole(AccountRole.USER);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveAccount(){
        AccountRequest.RegisterRequest request = createAccountRequest();
        Account account = createAccount(createAccountRequest());
        AccountResponse.RegisterResponse response = accountService.saveAccount(request);

        assertEquals(request.getEmail(), account.getEmail());
        assertEquals(request.getEmail(), response.getEmail());
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateAccountTest(){
        AccountRequest.RegisterRequest request1 = createAccountRequest();
        AccountRequest.RegisterRequest request2 = createAccountRequest();
        accountService.saveAccount(request1);

        Throwable e = assertThrows(EntityExistsException.class, () -> {
            accountService.saveAccount(request2);
        });

        assertEquals("존재하는 이메일입니다. 다른 이메일을 입력해 주세요.", e.getMessage());
    }
}