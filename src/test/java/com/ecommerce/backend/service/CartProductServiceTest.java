package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.enums.ProductStatus;
import com.ecommerce.backend.domain.request.CartProductRequest;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CartProductRepository;
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
class CartProductServiceTest {
    @InjectMocks
    CartProductService sut;

    @Mock
    CartProductRepository cartProductRepository;
    @Mock
    CartService cartService;
    @Mock
    ProductService productService;

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
            var product = makeProduct();

            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(productService.readById(anyLong())).willReturn(product);

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
            var product = makeProduct();
            var cartProduct = makeCartProduct(cart, product, 2);

            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(productService.readById(anyLong())).willReturn(product);

            given(sut.readByCartIdAndProductId(anyLong(), anyLong())).willReturn(cartProduct);

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
            var cartProduct1 = makeCartProduct(cart, makeProduct(), 1);
            var cartProduct2 = makeCartProduct(cart, makeProduct(), 2);
            var expected = List.of(cartProduct1, cartProduct2);

            given(cartProductRepository.findByCartId(anyLong())).willReturn(Optional.of(expected));

            // when
            final List<CartProduct> actual = sut.readByCartId(cart.getId());

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("아무 것도 못 불러온 케이스")
        void readByCartIdTest02() {
            // given
            var account = makeAccount();
            var cart = makeCart(account);

            given(cartProductRepository.findByCartId(anyLong())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByCartId(cart.getId()));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.PRODUCT_NOT_FOUND_IN_CART);
        }
    }

    @Nested
    @DisplayName("readByCartIdAndProductId 테스트")
    class ReadByCartIdAndProductIdTest {
        @Test
        @DisplayName("정상적으로 불러옴")
        void readByCartIdAndProductIdTest01() {
            // given
            var account = makeAccount();
            var cart = makeCart(account);
            var expected = makeCartProduct(cart, makeProduct(), 1);

            given(cartProductRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                    .willReturn(expected);

            // when
            final CartProduct actual = sut.readByCartIdAndProductId(1L, 1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("데이터가 없어서 못 불러옴")
        void readByCartIdAndProductIdTest02() {
            // when
            final CartProduct actual = sut.readByCartIdAndProductId(1L, 1L);

            // then
            assertNull(actual);
        }
    }

    @Nested
    @DisplayName("readByCartIdAndProductId 테스트")
    class ReadAllByCartIdAndProductIdListAndThumbnailTest {
        @Test
        @DisplayName("정상적으로 불러옴")
        void readAllByCartIdAndProductIdListAndThumbnailTest01() {
            // given
            var response1 = makeReadResponse(1L, "firstName", 100);
            var response2 = makeReadResponse(2L, "firstName", 200);
            var expected = List.of(response1, response2);

            given(cartProductRepository.findAllByCartIdAndProductIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
                    .willReturn(Optional.of(expected));

            // when
            final List<CartProductResponse.Read> actual =
                    sut.readAllByCartIdAndProductIdListAndThumbnail(1L, List.of(1L, 2L), true);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("데이터가 없어서 못 불러옴")
        void readAllByCartIdAndProductIdListAndThumbnailTest02() {
            // given
            var expected = new ArrayList<>();
            given(cartProductRepository.findAllByCartIdAndProductIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
                    .willReturn(Optional.empty());
            // when
            final List<CartProductResponse.Read> actual =
                    sut.readAllByCartIdAndProductIdListAndThumbnail(1L, List.of(1L, 2L), true);

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
            var cartProduct = makeCartProduct(cart, makeProduct(), 1);

            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(sut.readByCartIdAndProductId(anyLong(), anyLong())).willReturn(cartProduct);

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
            given(sut.readByCartIdAndProductId(anyLong(), anyLong())).willReturn(null);

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
            given(cartProductRepository.deleteByCartIdAndProductId(anyLong(), anyLong())).willReturn(expected);

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
            given(cartProductRepository.deleteByCartIdAndProductId(anyLong(), anyLong())).willReturn(expected);

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

            given(cartProductRepository.deleteByAccountId(anyLong())).willReturn(expected);

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

            given(cartProductRepository.deleteByAccountId(anyLong())).willReturn(expected);

            // when
            final int actual = sut.removeByAccountId(accountId);

            // then
            assertEquals(expected, actual);
        }
    }

    private CartProductRequest.Clear makeClearRequest(Long productId) {
        var request = new CartProductRequest.Clear();

        return request
                .setProductId(productId);
    }

    private CartProductRequest.Update makeUpdateRequest(Long productId, int count) {
        var request = new CartProductRequest.Update();

        return request
                .setProductId(productId)
                .setCount(count);
    }

    private CartProductResponse.Read makeReadResponse(Long productId, String productName, Integer price) {
        var response = new CartProductResponse.Read();

        return response.setProductId(productId)
                .setProductName(productName)
                .setPrice(price)
                .setCount(1)
                .setDescription("상품 설명")
                .setImageName("copy-이미지 이름")
                .setOriginImageName("이미지 이름")
                .setImageUrl("/thumbnail/");
    }


    private CartProduct makeCartProduct(Cart cart, Product product, int count) {
        return CartProduct.builder()
                .cart(cart)
                .product(product)
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

    private Product makeProduct() {
        return Product.builder()
                .id(1L)
                .productName("상품1")
                .price(1000)
                .quantity(100)
                .soldCount(2)
                .description("상품1 설명")
                .productStatus(ProductStatus.SOLD_OUT)
                .category(null)
                .build();
    }

    private CartProductRequest.Add makeAddRequest() {
        var request = new CartProductRequest.Add();
        return request
                .setProductId(1L)
                .setCount(30);
    }
}