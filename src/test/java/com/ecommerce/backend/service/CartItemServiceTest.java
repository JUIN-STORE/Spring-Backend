package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartItem;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.enums.ItemStatus;
import com.ecommerce.backend.domain.request.CartItemRequest;
import com.ecommerce.backend.domain.response.CartItemResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CartItemRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class CartItemServiceTest {
    @InjectMocks
    CartItemService sut;

    @Mock
    CartItemRepository cartItemRepository;
    @Mock
    CartService cartService;
    @Mock
    ItemService itemService;

    @Nested
    @DisplayName("add 테스트")
    class AddTest {
        @Test
        @DisplayName("카트에 처음으로 상품 추가할 때 생성 성공")
        void addTest01() {
            // given
            var expected = 30;
            var account = makeAccount();
            var request = makeAddRequest();

            var cart = makeCart(account);
            var item = makeItem();

            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(itemService.readById(anyLong())).willReturn(item);

            // when
            final int actual = sut.add(account, request);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("카트에 이미 제품이 있고 카트에 추가하기를 눌렀을 때")
        void addTest02() {
            // given
            var expected = 2 + 30;
            var account = makeAccount();
            var request = makeAddRequest();

            var cart = makeCart(account);
            var item = makeItem();
            var cartItem = makeCartItem(cart, item, 2);

            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(itemService.readById(anyLong())).willReturn(item);

            given(sut.readByCartIdAndItemId(anyLong(), anyLong())).willReturn(cartItem);

            // when
            final int actual = sut.add(account, request);

            // then
            assertEquals(expected, actual);
        }

        // FIXME: 실패 케이스는 뭐가 있을까?
    }


    @Nested
    @DisplayName("readByCartId 테스트")
    class ReadByCartIdTest {
        @Test
        @DisplayName("정상적으로 불러온 케이스")
        void readByCartIdTest01() {
            // given
            var account = makeAccount();
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
            var account = makeAccount();
            var cart = makeCart(account);

            given(cartItemRepository.findByCartId(anyLong())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByCartId(cart.getId()));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.PRODUCT_NOT_FOUND_IN_CART);
        }
    }

    @Nested
    @DisplayName("readByCartIdAndItemId 테스트")
    class ReadByCartIdAndItemIdTest {
        @Test
        @DisplayName("정상적으로 불러옴")
        void readByCartIdAndItemIdTest01() {
            // given
            var account = makeAccount();
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
    class ReadAllByCartIdAndItemIdListAndThumbnailTest {
        @Test
        @DisplayName("정상적으로 불러옴")
        void readAllByCartIdAndItemIdListAndThumbnailTest01() {
            // given
            var response1 = makeReadResponse(1L, "firstName", 100);
            var response2 = makeReadResponse(2L, "firstName", 200);
            var expected = List.of(response1, response2);

            given(cartItemRepository.findAllByCartIdAndItemIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
                    .willReturn(Optional.of(expected));

            // when
            final List<CartItemResponse.Read> actual =
                    sut.readAllByCartIdAndItemIdListAndThumbnail(1L, List.of(1L, 2L), true);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("데이터가 없어서 못 불러옴")
        void readAllByCartIdAndItemIdListAndThumbnailTest02() {
            // given
            var expected = new ArrayList<>();
            given(cartItemRepository.findAllByCartIdAndItemIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
                    .willReturn(Optional.empty());
            // when
            final List<CartItemResponse.Read> actual =
                    sut.readAllByCartIdAndItemIdListAndThumbnail(1L, List.of(1L, 2L), true);

            // then
            assertIterableEquals(expected, actual);
        }
    }


    @Nested
    @DisplayName("modifyQuantity 테스트")
    class ModifyQuantityTest {
        @Test
        @DisplayName("카트에 담긴 제품 수량 변경 성공")
        void modifyQuantityTest01() {
            // given
            var expected = 3;
            var account = makeAccount();
            var request = makeUpdateRequest(1L, expected);

            var cart = makeCart(account);
            var cartItem = makeCartItem(cart, makeItem(), 1);

            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(sut.readByCartIdAndItemId(anyLong(), anyLong())).willReturn(cartItem);

            // when
            final int actual = sut.modifyQuantity(account, request);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("카트에 담긴 제품 수량 변경 실패")
        void modifyQuantityTest02() {
            // given
            var expected = 3;
            var account = makeAccount();
            var request = makeUpdateRequest(1L, expected);

            var cart = makeCart(account);

            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(sut.readByCartIdAndItemId(anyLong(), anyLong())).willReturn(null);

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.modifyQuantity(account, request));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.CART_PRODUCT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("remove 테스트")
    class RemoveTest {
        @Test
        @DisplayName("카트(장바구니) 안에 있는 제품 삭제 성공")
        void removeTest01() {
            // given
            var expected = 1L;
            var account = makeAccount();
            var cart = makeCart(account);
            var request = makeClearRequest(1L);

            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(cartItemRepository.deleteByCartIdAndItemId(anyLong(), anyLong())).willReturn(expected);

            // when
            final long actual = sut.remove(account, request);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("카트(장바구니) 안에 있는 제품 삭제 실패")
        void removeTest02() {
            // given
            var expected = 0L;
            var account = makeAccount();
            var cart = makeCart(account);
            var request = makeClearRequest(1L);

            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(cartItemRepository.deleteByCartIdAndItemId(anyLong(), anyLong())).willReturn(expected);

            // when
            final long actual = sut.remove(account, request);

            // then
            assertEquals(expected, actual);
        }
    }


    @Nested
    @DisplayName("removeByAccountId 테스트 - 회원 탈퇴 때 사용")
    class RemoveByAccountIdTest {
        @Test
        @DisplayName("accountId로 삭제 성공")
        void removeByAccountIdTest01() {
            // given
            var expected = 1;
            var accountId= 9898L;

            given(cartItemRepository.deleteByAccountId(anyLong())).willReturn(expected);

            // when
            final int actual = sut.removeByAccountId(accountId);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("accountId로 삭제 실패")
        void removeByAccountIdTest02() {
            // given
            var expected = 0;
            var accountId= 9898L;

            given(cartItemRepository.deleteByAccountId(anyLong())).willReturn(expected);

            // when
            final int actual = sut.removeByAccountId(accountId);

            // then
            assertEquals(expected, actual);
        }
    }

    private CartItemRequest.Clear makeClearRequest(Long itemId) {
        var request = new CartItemRequest.Clear();

        return request
                .setItemId(itemId);
    }

    private CartItemRequest.Update makeUpdateRequest(Long itemId, int count) {
        var request = new CartItemRequest.Update();

        return request
                .setItemId(itemId)
                .setCount(count);
    }

    private CartItemResponse.Read makeReadResponse(Long itemId, String name, Integer price) {
        var response = new CartItemResponse.Read();

        return response.setItemId(itemId)
                .setName(name)
                .setPrice(price)
                .setCount(1)
                .setDescription("상품 설명")
                .setImageName("copy-이미지 이름")
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

    private CartItemRequest.Add makeAddRequest() {
        var request = new CartItemRequest.Add();
        return request
                .setItemId(1L)
                .setCount(30);
    }
}