package com.ecommerce.backend.service;

import com.ecommerce.backend.config.SecurityConfig;
import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.AddressRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    public static final String EMAIL = "js@gmail.com";

    @InjectMocks
    private AddressService sut;

    @Mock
    private AddressRepository addressRepository;

    @Nested
    @DisplayName("addOne Test")
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
    @DisplayName("addTwoParam Test")
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
    @DisplayName("addIfNull Test")
    class AddIfNullTest {
        @Test
        @DisplayName("불러온 주소가 null일 때")
        void addIfNullTest01() {
            // given
            var account = makeAccount(AccountRole.ADMIN);
            var request = makeRegisterRequest(false);
            given(addressRepository.findByAccountIdAndZipCodeAndCityAndStreet(anyLong(), any())).willReturn(null);

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

            given(addressRepository.findByAccountIdAndZipCodeAndCityAndStreet(anyLong(), any())).willReturn(expected);

            // when
            final Address actual = sut.addIfNull(account, request);

            // then
            assertEquals(expected, actual);
        }
    }


    @Nested
    @DisplayName("readById Method Test")
    class ReadByIdTest {
        @Test
        @DisplayName("정상적으로 address를 불러옴.")
        void readByIdTest01() {
            // given
            var expected = makeAddress(true);
            given(addressRepository.findById(anyLong())).willReturn(Optional.of(expected));

            // when
            final Address actual = sut.readById(expected.getId());

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("address 못 불러옴.")
        void readByIdTest02() {
            // given
            given(addressRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readById(1L));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ADDRESS_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readByIdAndAccountId Method Test")
    class ReadByIdAndAccountId {
        @Test
        @DisplayName("정상적으로 address를 불러옴.")
        void readByIdAndAccountId01() {
            // given
            var expected = makeAddress(true);
            given(addressRepository.findByIdAndAccountId(anyLong(), anyLong())).willReturn(Optional.of(expected));

            // when
            final Address actual = sut.readByIdAndAccountId(3L, 1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("address 못 불러옴.")
        void readByIdTest02() {
            // given
            given(addressRepository.findByIdAndAccountId(anyLong(), anyLong())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByIdAndAccountId(3L, 1L));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ADDRESS_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readByAccountId Method Test")
    class ReadByAccountId {
        @Test
        @DisplayName("정상적으로 address를 불러옴.")
        void readByAccountId01() {
            // given
            var expected = Arrays.asList(makeAddress(true), makeAddress(false));

            given(addressRepository.findByAccountId(anyLong())).willReturn(Optional.of(expected));

            // when
            final List<Address> actual = sut.readByAccountId(1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("address 못 불러옴.")
        void readByAccountId02() {
            // given
            given(addressRepository.findByAccountId(anyLong())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByAccountId(1L));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ADDRESS_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readByAccountIdAndDefaultAddress Method Test")
    class ReadByAccountIdAndDefaultAddress {
        @Test
        @DisplayName("정상적으로 address를 불러옴.")
        void readByAccountIdAndDefaultAddress01() {
            // given
            var expected = makeAddress(true);

            given(addressRepository.findByAccountIdAndDefaultAddress(anyLong())).willReturn(Optional.of(expected));

            // when
            final Address actual = sut.readByAccountIdAndDefaultAddress(1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("address 못 불러옴.")
        void readByAccountIdAndDefaultAddress02() {
            // given
            given(addressRepository.findByAccountIdAndDefaultAddress(anyLong())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByAccountIdAndDefaultAddress(1L));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ADDRESS_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readByAccountIdAndZipCodeAndCityAndStreet Method Test")
    class ReadByAccountIdAndZipCodeAndCityAndStreet {
        @Test
        @DisplayName("정상적으로 address를 불러옴.")
        void readByAccountIdAndZipCodeAndCityAndStreet01() {
            // given
            var expected = makeAddress(true);
            var request = makeRegisterRequest(false);

            given(addressRepository.findByAccountIdAndZipCodeAndCityAndStreet(anyLong(), any())).willReturn(expected);

            // when
            final Address actual = sut.readByAccountIdAndZipCodeAndCityAndStreet(1L, request);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("address 못 불러옴.")
        void readByAccountIdAndZipCodeAndCityAndStreet02() {
            // given
            given(addressRepository.findByAccountIdAndZipCodeAndCityAndStreet(anyLong(), any())).willReturn(null);

            // when
            final Address actual =
                    sut.readByAccountIdAndZipCodeAndCityAndStreet(1L, makeRegisterRequest(false));

            assertNull(actual);
        }
    }


    @Nested
    @DisplayName("modify Method Test")
    class ModifyTest {
        @Test
        @DisplayName("정상적으로 수정")
        void modifyTest01() {
            // given
            var expected = makeAddress(true);
            given(addressRepository.findByIdAndAccountId(anyLong(), anyLong())).willReturn(Optional.of(expected));

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

        @Test
        @DisplayName("address가 없어서 수정 불가")
        void modifyTest02() {
            // given
            var oldAddress = makeAddress(true);
            given(addressRepository.findByIdAndAccountId(anyLong(), anyLong())).willReturn(Optional.empty());

            var account = makeAccount(AccountRole.SELLER);
            var request = makeUpdateRequest(false);

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual
                    = assertThatThrownBy(() -> sut.modify(account, request));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ADDRESS_NOT_FOUND);
        }
    }


    @Nested
    @DisplayName("remove Method Test")
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
    @DisplayName("removeByAddressIdList Method Test")
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

    private AddressRequest.Update makeUpdateRequest(boolean isDefaultAddress) {
        final AddressRequest.Update request = new AddressRequest.Update();

        return request
                .setAddressId(3L)
                .setCity("update 서울시")
                .setStreet("update 강남구")
                .setZipCode(99999)
                .setDefaultAddress(isDefaultAddress);
    }

    private AddressRequest.Register makeRegisterRequest(boolean isDefaultAddress) {
        final AddressRequest.Register request = new AddressRequest.Register();

        return request
                .setCity("서울시")
                .setStreet("강남구")
                .setZipCode(12345)
                .setDefaultAddress(isDefaultAddress);
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

    private Address makeAddress(boolean isDefaultAddress) {
        return Address.builder()
                .id(3L)
                .city("서울시")
                .street("강남구")
                .zipCode(12345)
                .defaultAddress(isDefaultAddress)
                .build();
    }
}
