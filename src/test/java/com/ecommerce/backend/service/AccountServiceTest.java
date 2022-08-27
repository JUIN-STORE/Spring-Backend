package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.repository.AccountRepository;
import com.ecommerce.backend.repository.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService mockAccountService;

    @Mock private AccountRepository mockAccountRepository;
    @Mock private AddressRepository mockAddressRepository;
    @Mock private CartService mockCartService;

    @Test
    @DisplayName("중복 이메일 체크")
    void duplicateEmail() {
        // given
        String expected = "존재하는 이메일입니다. 다른 이메일을 입력해 주세요.";

        var entity = new Account().builder()
                .email("js@gmail.com")
                .build();

        var request = new AccountRequest.SignUp();
        request.setEmail("js@gmail.com");

        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(entity));

        // when
        EntityExistsException actual = Assertions.assertThrows(EntityExistsException.class, () -> mockAccountService.add(request));

        // then
        assertEquals(expected, actual.getMessage());
    }
}