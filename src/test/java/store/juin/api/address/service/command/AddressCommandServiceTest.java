package store.juin.api.address.service.command;//package com.ecommerce.backend.service.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.juin.api.account.enumeration.AccountRole;
import store.juin.api.account.model.entity.Account;
import store.juin.api.address.model.entity.Address;
import store.juin.api.address.model.request.AddressCreateRequest;
import store.juin.api.address.model.request.AddressUpdateRequest;
import store.juin.api.address.repository.jpa.AddressRepository;
import store.juin.api.address.service.query.AddressQueryService;
import store.juin.api.common.handler.CommandTransactional;
import store.juin.api.util.PasswordUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AddressCommandServiceTest {
    private static final String EMAIL = "js@gmail.com";

    @InjectMocks
    private AddressCommandService sut;

    @Spy
    private CommandTransactional commandTransactional;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressQueryService addressQueryService;

    @Nested
    @DisplayName("addOne 테스트")
    class AddOneParamTest {
        @Test
        @DisplayName("정상적으로 저장")
        void addOneParamTest01() {
            // given
            var expected = makeAddress(true);
            given(addressRepository.save(any(Address.class))).willReturn(expected);

            // when
            final Address actual = sut.add(expected);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("저장 실패")
        void addOneParamTest02() {
            // given
            var expected = makeAddress(true);
            given(addressRepository.save(any(Address.class))).willReturn(null);

            // when
            final Address actual = sut.add(expected);

            // then
            assertNull(actual);
        }
    }

    @Nested
    @DisplayName("addTwoParam 테스트")
    class AddTwoParamTest {
        @Test
        @DisplayName("정상적으로 저장")
        void addTwoParamTest01() {
            // given
            var expected = makeAddress(true);

            var account = makeAccount(AccountRole.USER);
            var request = makeRegisterRequest(true);
            given(addressRepository.save(any(Address.class))).willReturn(expected);

            // when
            final Address actual = sut.add(account, request);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("저장 실패")
        void addTest02() {
            // given
            var account = makeAccount(AccountRole.USER);
            var request = makeRegisterRequest(true);
            given(addressRepository.save(any(Address.class))).willReturn(null);

            // when
            final Address actual = sut.add(account, request);

            // then
            assertNull(actual);
        }
    }

    @Nested
    @DisplayName("addIfNull 테스트")
    class AddIfNullTest {
        @Test
        @DisplayName("불러온 주소가 null일 때")
        void addIfNullTest01() {
            // given
            var account = makeAccount(AccountRole.ADMIN);
            var request = makeRegisterRequest(false);
            given(addressQueryService.readByAccountIdAndZipCodeAndCityAndStreet(anyLong(), any())).willReturn(null);

            var expected = makeAddress(true);
            given(addressRepository.save(any(Address.class))).willReturn(expected);


            // when
            final Address actual = sut.addIfNull(account, request);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("불러온 주소가 null이 아닐 때")
        void addIfNullTest02() {
            // given
            var account = makeAccount(AccountRole.ADMIN);
            var request = makeRegisterRequest(false);
            var expected = makeAddress(true);

            given(addressQueryService.readByAccountIdAndZipCodeAndCityAndStreet(anyLong(), any())).willReturn(expected);

            // when
            final Address actual = sut.addIfNull(account, request);

            // then
            assertEquals(expected, actual);
        }
    }


    @Nested
    @DisplayName("modify 테스트")
    class ModifyTest {
        @Test
        @DisplayName("정상적으로 수정")
        void modifyTest01() {
            // given
            var expected = makeAddress(true);
            given(addressQueryService.readByIdAndAccountId(anyLong(), anyLong())).willReturn(expected);

            var account = makeAccount(AccountRole.SELLER);
            var request = makeUpdateRequest(false);
            var newAddress = request.toAddress(account);

            expected.dirtyChecking(newAddress);

            // when
            final Address actual = sut.modify(account, request);

            // then
            assertAll(
                    () -> assertEquals(expected.getId(), actual.getId()),
                    () -> assertEquals(expected.getCity(), actual.getCity()),
                    () -> assertEquals(expected.getStreet(), actual.getStreet()),
                    () -> assertEquals(expected.getZipCode(), actual.getZipCode())
            );
        }
    }


    @Nested
    @DisplayName("remove 테스트")
    class RemoveTest {
        @Test
        @DisplayName("주소 삭제 성공")
        void removeTest01() {
            // given
            var expected = 1L;
            given(addressRepository.delete(anyLong(), anyLong())).willReturn(1L);

            // when
            final long actual = sut.remove(1L, 3L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("주소 삭제 실패")
        void removeTest02() {
            // given
            var expected = 0L;
            given(addressRepository.delete(anyLong(), anyLong())).willReturn(0L);

            // when
            final long actual = sut.remove(1L, 3L);

            // then
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("removeByAddressIdList 테스트")
    class RemoveByAddressIdListTest {
        @Test
        @DisplayName("주소들 삭제 성공")
        void removeByAddressIdListTest01() {
            // given
            var expected = 1L;
            given(addressRepository.deleteByAddressIdList(anyLong(), anyList())).willReturn(expected);

            var addressIdList = List.of(3L, 5L);

            // when
            final long actual = sut.removeByAddressIdList(1L, addressIdList);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("주소들 삭제 실패")
        void removeByAddressIdListTest02() {
            // given
            var expected = 0L;
            given(addressRepository.deleteByAddressIdList(anyLong(), anyList())).willReturn(expected);

            var addressIdList = List.of(3L, 5L);

            // when
            final long actual = sut.removeByAddressIdList(1L, addressIdList);

            // then
            assertEquals(expected, actual);
        }
    }

    private AddressUpdateRequest makeUpdateRequest(boolean defaultAddress) {
        final AddressUpdateRequest request = new AddressUpdateRequest();

        return request
                .setAddressId(3L)
                .setCity("update 서울시")
                .setStreet("update 강남구")
                .setZipCode(99999)
                .setDefaultAddress(defaultAddress);
    }

    private AddressCreateRequest makeRegisterRequest(boolean defaultAddress) {
        final AddressCreateRequest request = new AddressCreateRequest();

        return request
                .setCity("서울시")
                .setStreet("강남구")
                .setZipCode(12345)
                .setDefaultAddress(defaultAddress);
    }

    private Account makeAccount(AccountRole accountRole) {
        return Account.builder()
                .id(1L)
                .email(EMAIL)
                .passwordHash(PasswordUtil.makePasswordHash("passwordHash"))
                .name("지수")
                .accountRole(accountRole)
                .phoneNumber("01011112222")
                .build();
    }

    private Address makeAddress(boolean defaultAddress) {
        return Address.builder()
                .id(3L)
                .city("서울시")
                .street("강남구")
                .zipCode(12345)
                .defaultAddress(defaultAddress)
                .build();
    }
}
