package store.juin.api.address.service.query;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.juin.api.address.model.entity.Address;
import store.juin.api.address.repository.jpa.AddressRepository;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.QueryTransactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static store.juin.api.common.RequestUtil.makeCreateRequest;

@ExtendWith(MockitoExtension.class)
class AddressQueryServiceTest {
    @InjectMocks
    private AddressQueryService sut;

    @Spy
    private QueryTransactional queryTransactional;

    @Mock
    private AddressRepository addressRepository;

    @Nested
    @DisplayName("readById 테스트")
    class RetrieveByIdTest {
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
    @DisplayName("readByIdAndAccountId 테스트")
    class RetrieveByIdAndAccountId {
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
    @DisplayName("readByAccountId 테스트")
    class RetrieveByAccountId {
        @Test
        @DisplayName("정상적으로 address를 불러옴.")
        void readByAccountId01() {
            // given
            var expected = Arrays.asList(makeAddress(true), makeAddress(false));

            given(addressRepository.findAllByAccountId(anyLong())).willReturn(Optional.of(expected));

            // when
            final List<Address> actual = sut.readAllByAccountId(1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("address 못 불러옴.")
        void readByAccountId02() {
            // given
            given(addressRepository.findAllByAccountId(anyLong())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readAllByAccountId(1L));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ADDRESS_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readByAccountIdAndDefaultAddress 테스트")
    class RetrieveByAccountIdAndDefaultAddress {
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
    @DisplayName("readByAccountIdAndZipCodeAndCityAndStreet 테스트")
    class RetrieveByAccountIdAndZipCodeAndCityAndStreet {
        @Test
        @DisplayName("정상적으로 address를 불러옴.")
        void readByAccountIdAndZipCodeAndCityAndStreet01() {
            // given
            var expected = makeAddress(true);
            var request = makeCreateRequest(false);

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
                    sut.readByAccountIdAndZipCodeAndCityAndStreet(1L, makeCreateRequest(false));

            assertNull(actual);
        }
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
