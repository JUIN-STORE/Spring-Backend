package com.ecommerce.backend.service;

import com.ecommerce.backend.config.SecurityConfig;
import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    public static final String EMAIL = "js@gmail.com";

    @InjectMocks
    private AccountService sut;

    @Mock
    private AccountRepository mockAccountRepository;
    @Mock
    private JwtService mockJwtService;
    @Mock
    private AddressService mockAddressService;
    @Mock
    private CartService mockCartService;
    @Mock
    private CartItemService mockCartItemService;
    @Mock
    private DeliveryService mockDeliveryService;
    @Mock
    private OrderService mockOrderService;
    @Mock
    private OrderItemService mockOrderItemService;

    @Nested
    @DisplayName("duplicateEmail 테스트")
    class DuplicateEmailTest {
        @Test
        @DisplayName("중복된 이메일이 있을 때")
        void duplicateEmailTest01() throws Exception {
            // given
            String expected = "존재하는 이메일입니다. 다른 이메일을 입력해 주세요.";

            var account = makeAccount(AccountRole.USER);
            var request = makeSignUpRequest();

            given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(account));

            // when
            Method method =
                    AccountService.class.getDeclaredMethod("checkDuplicatedEmail", AccountRequest.SignUp.class);
            method.setAccessible(true);
            var actual = assertThatThrownBy(() -> {
                        try {
                            method.invoke(sut, request);
                        } catch (InvocationTargetException e) {
                            throw e.getCause();
                        }
                    }
            );

            // then
            actual.isInstanceOf(EntityExistsException.class).hasMessage(expected);
        }

        @Test
        @DisplayName("중복된 이메일이 없을 때")
        void duplicateEmailTest02() throws Exception {
            // given
            var request = makeSignUpRequest();

            given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.empty());

            // when, then
            Method method =
                    AccountService.class.getDeclaredMethod("checkDuplicatedEmail", AccountRequest.SignUp.class);
            method.setAccessible(true);
            method.invoke(sut, request);
        }
    }

    @Nested
    @DisplayName("add 테스트")
    class AddTest {
        @Test
        @DisplayName("정상 케이스")
        void addTest01() {
            // given
            MockedStatic<SecurityConfig> mockSecurityConfig = mockStatic(SecurityConfig.class);
            given(SecurityConfig.makePasswordHash(any())).willReturn("securityPasswordHash");

            final var accountRequest = makeSignUpRequest();
            final var expected = makeAccount(AccountRole.USER);

            // when
            final Account actual = sut.add(accountRequest);

            // then
            assertEquals(expected.getEmail(), actual.getEmail());
            assertEquals(expected.getPasswordHash(), actual.getPasswordHash());

            mockSecurityConfig.close();
        }
    }

    @Nested
    @DisplayName("readById 테스트")
    class ReadByIdTest {
        @Test
        @DisplayName("정상 케이스")
        void readByIdTest01() {
            // given
            final var expected = makeAccount(AccountRole.USER);
            given(mockAccountRepository.findById(any())).willReturn(Optional.of(expected));

            // when
            final Account actual = sut.readById(1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("존재하지 않는 계정일 때")
        void readByIdTest02() {
            // given
            given(mockAccountRepository.findById(any())).willReturn(Optional.empty());

            // when
            var actual =
                    assertThatThrownBy(() -> sut.readById(1L));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ACCOUNT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readByEmail 테스트")
    class ReadByEmailTest {
        @Test
        @DisplayName("정상 케이스")
        void readByEmailTest01() {
            // given
            final var expected = makeAccount(AccountRole.USER);
            given(mockAccountRepository.findByEmail(any())).willReturn(Optional.of(expected));

            // when
            final Account actual = sut.readByEmail(EMAIL);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("존재하지 않는 계정일 때")
        void readByEmailTest02() {
            // given
            given(mockAccountRepository.findByEmail(any())).willReturn(Optional.empty());

            // when
            var actual =
                    assertThatThrownBy(() -> sut.readByEmail(EMAIL));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ACCOUNT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("modify 테스트")
    class ModifyTest {
        @Test
        @DisplayName("정상적으로 수정 성공")
        void modifyTest01() {
            // given
            final Principal mockPrincipal = Mockito.mock(Principal.class);
            final AccountRequest.Update request = makeUpdateRequest();

            final Account expected = makeAccount(AccountRole.USER);
            request.toAccount(expected.getId(), EMAIL);

            given(mockPrincipal.getName()).willReturn(EMAIL);
            given(mockAccountRepository.findByEmail(any())).willReturn(Optional.of(expected));

            // when
            final Account actual = sut.modify(mockPrincipal, request);

            // then
            verify(mockAccountRepository, times(1)).save(any());
            assertEquals(expected.getEmail(), actual.getEmail());

            assertNotEquals(expected.getPasswordHash(), actual.getPasswordHash());
            assertNotEquals(expected.getName(), actual.getName());
            assertNotEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
            assertNotEquals(expected.getAccountRole(), actual.getAccountRole());
        }

        @Test
        @DisplayName("account를 못 찾아서 수정 실패")
        void modifyTest02() {
            // given
            final Principal mockPrincipal = Mockito.mock(Principal.class);
            final AccountRequest.Update request = makeUpdateRequest();

            final Account expected = makeAccount(AccountRole.USER);
            request.toAccount(expected.getId(), EMAIL);

            given(mockPrincipal.getName()).willReturn(EMAIL);
            given(mockAccountRepository.findByEmail(any())).willReturn(Optional.empty());

            // when
            var actual =
                    assertThatThrownBy(() -> sut.modify(mockPrincipal, request));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ACCOUNT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("remove 테스트")
    class RemoveTest {
        @Test
        @DisplayName("정상적으로 삭제 성공")
        void removeTest01() {
            // given
            final Principal mockPrincipal = Mockito.mock(Principal.class);
            given(mockPrincipal.getName()).willReturn(EMAIL);

            final Account account = makeAccount(AccountRole.USER);

            List<Address> addressList =
                    Arrays.asList(makeAddress(1L, true), makeAddress(3L,false));
            given(mockAccountRepository.findByIdAndEmail(anyLong(), anyString())).willReturn(Optional.of(account));

            List<Order> orderList =
                    Arrays.asList(makeOrder(1L), makeOrder(10L));
            given(mockAddressService.readByAccountId(anyLong())).willReturn(addressList);

            // when
            sut.remove(mockPrincipal, account.getId());

            // then
            verify(mockAccountRepository, times(1)).delete(any());
        }
    }

    @Nested
    @DisplayName("readByEmail 테스트")
    class ReadByIdAndEmailTest {
        @Test
        @DisplayName("id와 email을 통해 계정을 찾음")
        void readByIdAndEmailTest01() {
            // given
            final var expected = makeAccount(AccountRole.USER);
            given(mockAccountRepository.findByIdAndEmail(anyLong(), anyString())).willReturn(Optional.of(expected));

            // when
            final Account actual = sut.readByIdAndEmail(1L,EMAIL);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("id와 email을 통해 계정을 찾지 못함")
        void readByEmailTest02() {
            // given
            given(mockAccountRepository.findByIdAndEmail(anyLong(), anyString())).willReturn(Optional.empty());

            // when
            var actual =
                    assertThatThrownBy(() -> sut.readByIdAndEmail(1L, EMAIL));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ACCOUNT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("checkNotUser 테스트")
    class CheckNotUser {
        @Test
        @DisplayName("ADMIN일 떄")
        void checkNotUserTest01() {
            // given
            final Principal mockPrincipal = Mockito.mock(Principal.class);
            given(mockJwtService.readByPrincipal(mockPrincipal)).willReturn(makeAccount(AccountRole.ADMIN));

            // when
            final boolean actual = sut.checkNotUser(mockPrincipal);

            // then
            assertTrue(actual);
        }

        @Test
        @DisplayName("SELLERN일 떄")
        void checkNotUserTest02() {
            // given
            final Principal mockPrincipal = Mockito.mock(Principal.class);
            given(mockJwtService.readByPrincipal(mockPrincipal)).willReturn(makeAccount(AccountRole.SELLER));

            // when
            final boolean actual = sut.checkNotUser(mockPrincipal);

            // then
            assertTrue(actual);
        }

        @Test
        @DisplayName("USER일 떄")
        void checkNotUserTest03() {
            // given
            final Principal mockPrincipal = Mockito.mock(Principal.class);
            given(mockJwtService.readByPrincipal(mockPrincipal)).willReturn(makeAccount(AccountRole.USER));

            // when
            final boolean actual = sut.checkNotUser(mockPrincipal);

            // then
            assertFalse(actual);
        }
    }

    private Order makeOrder(Long id) {
        return Order.builder()
                .id(id)
                .build();
    }

    private AccountRequest.Update makeUpdateRequest() {
        final AccountRequest.Update request = new AccountRequest.Update();
        request.setAccountRole(AccountRole.ADMIN);
        request.setPasswordHash("updatePasswordHash");
        request.setName("updateName");
        request.setPhoneNumber("01099998888");
        request.setAccountRole(AccountRole.SELLER);

        return request;
    }


    private AccountRequest.SignUp makeSignUpRequest() {
        final var request = new AccountRequest.SignUp();
        request.setEmail(EMAIL);
        request.setPasswordHash("passwordHash");
        request.setName("지수");
        request.setAccountRole(AccountRole.USER);
        request.setPhoneNumber("01011112222");

        final var addressRequest = new AddressRequest.Register();
        addressRequest.setCity("서울시");
        addressRequest.setStreet("강남구");
        addressRequest.setZipCode(12345);
        request.setAddress(addressRequest);

        return request;
    }

    private Address makeAddress(Long id, boolean isDefaultAddress) {
        return Address.builder()
                .id(id)
                .city("서울시 " + isDefaultAddress)
                .street("강남구 " + isDefaultAddress)
                .zipCode(12345)
                .defaultAddress(isDefaultAddress)
                .build();
    }

    private Account makeAccount(AccountRole accountRole) {
        return Account.builder()
                .id(1L)
                .email(EMAIL)
                .passwordHash(SecurityConfig.makePasswordHash("passwordHash"))
                .name("지수")
                .accountRole(accountRole)
                .phoneNumber("01011112222")
                .build();
    }
}