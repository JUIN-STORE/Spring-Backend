package store.juin.api.cartitem.service.query;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import store.juin.api.account.model.entity.Account;
import store.juin.api.cart.model.entity.Cart;
import store.juin.api.cartitem.model.entity.CartItem;
import store.juin.api.cartitem.model.response.CartItemResponse;
import store.juin.api.cartitem.repository.jpa.CartItemRepository;
import store.juin.api.cartitem.service.CartItemQueryService;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.QueryTransactional;
import store.juin.api.item.enumeration.ItemStatus;
import store.juin.api.item.model.entity.Item;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static store.juin.api.common.EntityUtil.makeAccount;

@ExtendWith(SpringExtension.class)
class CartItemQueryServiceTest {
    @InjectMocks
    private CartItemQueryService sut;

    @Spy
    private QueryTransactional queryTransactional;

    @Mock
    CartItemRepository cartItemRepository;

    @Nested
    @DisplayName("readByCartId 테스트")
    class RetrieveByCartIdTest {
        @Test
        @DisplayName("정상적으로 불러온 케이스")
        void readByCartIdTest01() {
            // given
            var account = makeAccount("asdq13@#13$");
            var cart = makeCart(account);
            var cartItem1 = makeCartItem(cart, makeItem(), 1);
            var cartItem2 = makeCartItem(cart, makeItem(), 2);
            var expected = List.of(cartItem1, cartItem2);

            given(cartItemRepository.findByCartId(anyLong())).willReturn(Optional.of(expected));

            // when
            final List<CartItem> actual = sut.readByCartId(cart.getId());

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("아무 것도 못 불러온 케이스")
        void readByCartIdTest02() {
            // given
            var account = makeAccount("asdq13@#13$");
            var cart = makeCart(account);

            given(cartItemRepository.findByCartId(anyLong())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByCartId(cart.getId()));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ITEM_NOT_FOUND_IN_CART);
        }
    }

    @Nested
    @DisplayName("readByCartIdAndItemId 테스트")
    class RetrieveByCartIdAndItemIdTest {
        @Test
        @DisplayName("정상적으로 불러옴")
        void readByCartIdAndItemIdTest01() {
            // given
            var account = makeAccount("asdq13@#13$");
            var cart = makeCart(account);
            var expected = makeCartItem(cart, makeItem(), 1);

            given(cartItemRepository.findByCartIdAndItemId(anyLong(), anyLong()))
                    .willReturn(expected);

            // when
            final CartItem actual = sut.readByCartIdAndItemId(1L, 1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("데이터가 없어서 못 불러옴")
        void readByCartIdAndItemIdTest02() {
            // when
            final CartItem actual = sut.readByCartIdAndItemId(1L, 1L);

            // then
            assertNull(actual);
        }
    }

    @Nested
    @DisplayName("readByCartIdAndItemId 테스트")
    class RetrieveAllByCartIdAndItemIdListAndThumbnailTest {
        @Test
        @DisplayName("정상적으로 불러옴")
        void readAllByCartIdAndItemIdListAndThumbnailTest01() {
            // given
            var response1 = makeReadResponse(1L, "firstName", 100);
            var response2 = makeReadResponse(2L, "firstName", 200);
            var expected = List.of(response1, response2);

            boolean representative = true;
            given(cartItemRepository.findAllByCartIdAndItemIdListAndThumbnail(anyLong(), anyList(), anyBoolean(), anyBoolean()))
                    .willReturn(Optional.of(expected));

            // when
            final List<CartItemResponse.Retrieve> actual =
                    sut.readAllByCartIdAndItemIdListAndThumbnail(1L, List.of(1L, 2L), true, representative);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("데이터가 없어서 못 불러옴")
        void readAllByCartIdAndItemIdListAndThumbnailTest02() {
            // given
            var expected = new ArrayList<>();
            boolean representative = true;
            given(cartItemRepository.findAllByCartIdAndItemIdListAndThumbnail(anyLong(), anyList(), anyBoolean(), anyBoolean()))
                    .willReturn(Optional.empty());
            // when
            final List<CartItemResponse.Retrieve> actual =
                    sut.readAllByCartIdAndItemIdListAndThumbnail(1L, List.of(1L, 2L), true, representative);

            // then
            assertIterableEquals(expected, actual);
        }
    }

    private CartItemResponse.Retrieve makeReadResponse(Long itemId, String itemName, Integer price) {
        var response = new CartItemResponse.Retrieve();

        return response.setItemId(itemId)
                .setItemName(itemName)
                .setPrice(price)
                .setCount(1)
                .setDescription("상품 설명")
                .setItemImageName("copy-이미지 이름")
                .setOriginImageName("이미지 이름")
                .setImageUrl("/thumbnail/");
    }


    private CartItem makeCartItem(Cart cart, Item item, int count) {
        return CartItem.builder()
                .cart(cart)
                .item(item)
                .count(count)
                .build();
    }

    private Cart makeCart(Account account) {
        return Cart.builder()
                .id(1L)
                .account(account)
                .build();
    }

    private Item makeItem() {
        return Item.builder()
                .id(1L)
                .name("상품1")
                .price(1000)
                .quantity(100)
                .soldCount(2)
                .description("상품1 설명")
                .itemStatus(ItemStatus.SOLD_OUT)
                .category(null)
                .build();
    }
}
