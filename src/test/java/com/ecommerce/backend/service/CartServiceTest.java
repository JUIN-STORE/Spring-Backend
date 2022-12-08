package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CartRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CartServiceTest {
    @InjectMocks
    CartService sut;

    @Mock
    CartRepository cartRepository;


    @Nested
    @DisplayName("add 테스트")
    class AddTest {
        @Test
        @DisplayName("생성 정상")
        void addTest01() {
            // given
            var account = makeAccount();
            var cart = makeCart(account);

            MockedStatic<Cart> mockCart = mockStatic(Cart.class);
            given(Cart.createCart(any())).willReturn(cart);

            // when
            sut.add(account);

            // then
            verify(cartRepository, times(1)).save(cart);

            mockCart.close();
        }

        @Test
        @DisplayName("생성 실패")
        void addTest02() {
            // given
            var account = makeAccount();
            var cart = makeCart(account);

            MockedStatic<Cart> mockCart = mockStatic(Cart.class);
            given(Cart.createCart(any())).willReturn(null);

            // when
            sut.add(account);

            // then
            verify(cartRepository, times(0)).save(cart);

            mockCart.close();
        }
    }

    @Nested
    @DisplayName("readByAccountId 테스트")
    class ReadByAccountIdTest {
        @Test
        @DisplayName("정상 케이스")
        void readByAccountIdTest01() {
            // given
            var account = makeAccount();
            var expected = makeCart(account);
            given(cartRepository.findByAccountId(any())).willReturn(Optional.of(expected));

            // when
            final Cart actual = sut.readByAccountId(account.getId());

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("카트를 찾지 못할 때")
        void readByAccountIdTest02() {
            // given
            var expected = 1L;
            given(cartRepository.findByAccountId(any())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByAccountId(expected));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.CART_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("deleteByAccountId 테스트")
    class DeleteByAccountIdTest {
        @Test
        @DisplayName("삭제 성공")
        void deleteByAccountIdTest01() {
            // given
            var expected = 1L;
            given(cartRepository.deleteByAccountId(any())).willReturn(expected);

            // when
            final long actual = sut.removeByAccountId(expected);

            // then
            assertEquals(expected, actual);
        }

        // FIXME: 쿼리 오류일 때 Exception 뱉는 거 고려하기
        @Test
        @DisplayName("where절에 매칭되는 게 없어서 삭제 실패")
        void deleteByAccountIdTest02() {
            // given
            var expected = 0L;
            given(cartRepository.deleteByAccountId(any())).willReturn(expected);

            // when
            final long actual = sut.removeByAccountId(expected);

            // then
            assertEquals(expected, actual);
        }
    }


    private Account makeAccount() {
        return Account.builder()
                .id(1L)
                .build();
    }

    private Cart makeCart(Account account) {
        return Cart.builder()
                .id(1L)
                .account(account)
                .build();
    }
}