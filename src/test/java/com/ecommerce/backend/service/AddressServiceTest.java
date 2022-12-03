package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.repository.jpa.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @InjectMocks
    private AddressService sut;

    @Mock
    private AddressRepository addressRepository;

    @Nested
    @DisplayName("정상 케이스")
    class Success {
        private Address address;

        @BeforeEach
        void setUp() {
            address = Address.builder()
                    .id(1L)
                    .city("서울시")
                    .street("강남구")
                    .zipCode(12345)
                    .defaultAddress(false)
                    .build();
        }

        @Test
        @DisplayName("save")
        void saveTest() {
            // given
            given(addressRepository.save(any(Address.class))).willReturn(address);

            // when
            final Address actual = sut.add(address);

            // then
            assertThat(actual).isEqualTo(address);
        }

        @Test
        @DisplayName("readById")
        void readByIdTest() {
            // given
            given(addressRepository.findById(any(Long.class))).willReturn(Optional.of(address));

            // when
            final Address actual = sut.readByIdAndAccountId(1L, 1L);

            // then
            assertThat(actual).isEqualTo(address);
        }

        // FIXME: 테스트 코드 더 작성해야 됨.
    }

}
