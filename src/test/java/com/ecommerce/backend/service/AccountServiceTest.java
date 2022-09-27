package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.repository.jpa.AccountRepository;
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
    private AccountService sut;

    @Mock private AccountRepository mockAccountRepository;
    @Mock private AddressService mockAddressService;
    @Mock private CartService mockCartService;

    @Test
    @DisplayName("정상적으로 회원가입")
    void saveSuccess() {
        // given
        final var accountRequest = new AccountRequest.SignUp();
        accountRequest.setEmail("js@gmail.com");
        accountRequest.setName("준수");
        accountRequest.setAccountRole(AccountRole.USER);
        accountRequest.setPhoneNumber("01011112222");

        final var addressRequest = new AddressRequest.Register();
        addressRequest.setCity("서울시");
        addressRequest.setStreet("동작구");
        addressRequest.setZipCode(12345);

        accountRequest.setAddress(addressRequest);

        // when


        // then
    }


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
        EntityExistsException actual = Assertions.assertThrows(EntityExistsException.class, () -> sut.add(request));

        // then
        assertEquals(expected, actual.getMessage());
    }


}