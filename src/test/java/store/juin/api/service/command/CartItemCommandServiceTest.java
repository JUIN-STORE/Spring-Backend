package store.juin.api.service.command;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Cart;
import store.juin.api.domain.entity.CartItem;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.enums.ItemStatus;
import store.juin.api.domain.request.CartItemRequest;
import store.juin.api.domain.response.CartItemResponse;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.CartItemRepository;
import store.juin.api.service.query.CartItemQueryService;
import store.juin.api.service.query.CartQueryService;
import store.juin.api.service.query.ItemQueryService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static store.juin.api.domain.EntityUtil.makeAccount;

@ExtendWith(SpringExtension.class)
class CartItemCommandServiceTest {
    @InjectMocks
    private CartItemCommandService sut;

    @Mock private CartItemRepository cartItemRepository;

    @Mock private CartQueryService cartQueryService;
    @Mock private ItemQueryService itemQueryService;
    @Mock private CartItemQueryService cartItemQueryService;

    @Nested
    @DisplayName("add 테스트")
    class AddTest {
        @Test
        @DisplayName("카트에 처음으로 상품 추가할 때 생성 성공")
        void addTest01() {
            // given
            var expected = 30;
            var account = makeAccount("asdq13@#13$");
            var request = makeAddRequest();

            var cart = makeCart(account);
            var item = makeItem();

            given(cartQueryService.readByAccountId(anyLong())).willReturn(cart);
            given(itemQueryService.readById(anyLong())).willReturn(item);

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
            var account = makeAccount("asdq13@#13$");
            var request = makeAddRequest();

            var cart = makeCart(account);
            var item = makeItem();
            var cartItem = makeCartItem(cart, item, 2);

            given(cartQueryService.readByAccountId(anyLong())).willReturn(cart);
            given(itemQueryService.readById(anyLong())).willReturn(item);
            given(cartItemQueryService.readByCartIdAndItemId(anyLong(), anyLong())).willReturn(cartItem);

            // when
            final int actual = sut.add(account, request);

            // then
            assertEquals(expected, actual);
        }

        // FIXME: 실패 케이스는 뭐가 있을까?
    }


    @Nested
    @DisplayName("modifyQuantity 테스트")
    class ModifyQuantityTest {
        @Test
        @DisplayName("카트에 담긴 제품 수량 변경 성공")
        void modifyQuantityTest01() {
            // given
            var expected = 3;
            var account = makeAccount("asdq13@#13$");
            var request = makeUpdateRequest(1L, expected);

            var cart = makeCart(account);
            var cartItem = makeCartItem(cart, makeItem(), 1);

            given(cartQueryService.readByAccountId(anyLong())).willReturn(cart);
            given(cartItemQueryService.readByCartIdAndItemId(anyLong(), anyLong())).willReturn(cartItem);

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
            var account = makeAccount("asdq13@#13$");
            var request = makeUpdateRequest(1L, expected);

            var cart = makeCart(account);

            given(cartQueryService.readByAccountId(anyLong())).willReturn(cart);
            given(cartItemQueryService.readByCartIdAndItemId(anyLong(), anyLong())).willReturn(null);

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.modifyQuantity(account, request));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.CART_ITEM_NOT_FOUND);
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
            var account = makeAccount("asdq13@#13$");
            var cart = makeCart(account);
            var request = List.of(1L, 2L);

            given(cartQueryService.readByAccountId(anyLong())).willReturn(cart);
            given(cartItemRepository.deleteByCartIdAndItemId(anyLong(), anyList())).willReturn(expected);

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
            var account = makeAccount("asdq13@#13$");
            var cart = makeCart(account);
            var request = List.of(1L, 2L);

            given(cartQueryService.readByAccountId(anyLong())).willReturn(cart);
            given(cartItemRepository.deleteByCartIdAndItemId(anyLong(), anyList())).willReturn(expected);

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

    private CartItemRequest.Add makeAddRequest() {
        var request = new CartItemRequest.Add();
        return request
                .setItemId(1L)
                .setCount(30);
    }
}
