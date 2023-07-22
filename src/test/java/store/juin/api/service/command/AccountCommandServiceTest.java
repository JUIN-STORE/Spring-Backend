package store.juin.api.service.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Address;
import store.juin.api.domain.enums.AccountRole;
import store.juin.api.domain.request.AccountRequest;
import store.juin.api.domain.request.AddressRequest;
import store.juin.api.domain.response.OrderResponse;
import store.juin.api.exception.Msg;
import store.juin.api.handler.CommandTransactional;
import store.juin.api.repository.jpa.AccountRepository;
import store.juin.api.service.query.AddressQueryService;
import store.juin.api.utils.PasswordUtil;

import javax.persistence.EntityExistsException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountCommandServiceTest {
    private static final String EMAIL = "js@gmail.com";

    @InjectMocks
    private AccountCommandService sut;

    @Spy
    private CommandTransactional commandTransactional;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AddressQueryService addressQueryService;

    @Mock
    private CartCommandService cartCommandService;
    @Mock
    private OrderCommandService orderCommandService;
    @Mock
    private AddressCommandService addressCommandService;
    @Mock
    private CartItemCommandService cartItemCommandService;
    @Mock
    private DeliveryCommandService deliveryCommandService;

    @Nested
    @DisplayName("duplicateEmail 테스트")
    class DuplicateEmailTest {
        @Test
        @DisplayName("중복된 이메일이 있을 때")
        void duplicateEmailTest01() throws Exception {
            // given
            var account = makeAccount(AccountRole.USER);
            var request = makeSignUpRequest();

            given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(account));

            // when
            Method method =
                    AccountCommandService.class.getDeclaredMethod("checkDuplicatedEmail", AccountRequest.SignUp.class);
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
            actual.isInstanceOf(EntityExistsException.class).hasMessage(Msg.DUPLICATED_ACCOUNT);
        }

        @Test
        @DisplayName("중복된 이메일이 없을 때")
        void duplicateEmailTest02() throws Exception {
            // given
            var request = makeSignUpRequest();

            given(accountRepository.findByEmail(anyString())).willReturn(Optional.empty());

            // when, then
            Method method =
                    AccountCommandService.class.getDeclaredMethod("checkDuplicatedEmail", AccountRequest.SignUp.class);
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
            MockedStatic<PasswordUtil> mockPasswordUtil = mockStatic(PasswordUtil.class);
            given(PasswordUtil.makePasswordHash(any())).willReturn("securityPasswordHash");

            var accountRequest = makeSignUpRequest();
            var expected = makeAccount(AccountRole.USER);

            // when
            final Account actual = sut.add(accountRequest);

            // then
            assertEquals(expected.getEmail(), actual.getEmail());
            assertEquals(expected.getPasswordHash(), actual.getPasswordHash());

            mockPasswordUtil.close();
        }
    }

    @Nested
    @DisplayName("modify 테스트")
    class ModifyTest {
        @Test
        @DisplayName("정상적으로 수정 성공")
        void modifyTest01() {
            // given
            var account = makeAccount(AccountRole.USER);
            var expected = makeUpdateRequest();

            // when
            final Account actual = sut.modify(account, expected);

            // then
            assertAll(
                    () -> assertNotEquals(expected.getPasswordHash(), actual.getPasswordHash()),
                    () -> assertEquals(expected.getName(), actual.getName()),
                    () -> assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber()),
                    () -> Assertions.assertEquals(expected.getAccountRole(), actual.getAccountRole())
            );
        }
    }

    @Nested
    @DisplayName("remove 테스트")
    class RemoveTest {
        @Test
        @DisplayName("정상적으로 삭제 성공")
        void removeTest01() {
            // given
            var account = makeAccount(AccountRole.USER);

            var addressList =
                    Arrays.asList(makeAddress(1L, true), makeAddress(3L, false));

            var deleteResponse = new OrderResponse.Delete()
                    .setOrdersDeletedCount(1)
                    .setOrderItemDeletedCount(1);
            given(addressQueryService.readAllByAccountId(anyLong())).willReturn(addressList);
            given(orderCommandService.remove(anyLong())).willReturn(deleteResponse);

            // when
            sut.remove(account, account.getId());

            // then
            verify(accountRepository, times(1)).delete(any());
        }
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

        final var addressRequest = new AddressRequest.Create();
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
                .identification("juin")
                .email(EMAIL)
                .passwordHash(PasswordUtil.makePasswordHash("passwordHash"))
                .name("지수")
                .accountRole(accountRole)
                .phoneNumber("01011112222")
                .build();
    }
}