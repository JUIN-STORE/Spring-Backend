package store.juin.api.service.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Cart;
import store.juin.api.handler.CommandTransactional;
import store.juin.api.repository.jpa.CartRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static store.juin.api.domain.EntityUtil.makeAccount;

@ExtendWith(SpringExtension.class)
class CartCommandServiceTest {
    @InjectMocks
    private CartCommandService sut;

    @Spy
    private CommandTransactional commandTransactional;

    @Mock
    private CartRepository cartRepository;

    @Nested
    @DisplayName("add 테스트")
    class AddTest {
        @Test
        @DisplayName("생성 정상")
        void addTest01() {
            // given
            var account = makeAccount("asdq13@#13$");
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
            var account = makeAccount("asdq13@#13$");
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

    private Cart makeCart(Account account) {
        return Cart.builder()
                .id(1L)
                .account(account)
                .build();
    }
}