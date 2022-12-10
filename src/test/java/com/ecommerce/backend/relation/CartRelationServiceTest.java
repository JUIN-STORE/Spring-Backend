package com.ecommerce.backend.relation;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.enums.ProductStatus;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.ecommerce.backend.domain.response.ProductImageResponse;
import com.ecommerce.backend.domain.response.ProductResponse;
import com.ecommerce.backend.service.CartProductService;
import com.ecommerce.backend.service.CartService;
import com.ecommerce.backend.service.relation.CartRelationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(SpringExtension.class)
class CartRelationServiceTest {
    @InjectMocks
    CartRelationService sut;

    @Mock
    CartService cartService;
    @Mock
    CartProductService cartProductService;

    @Nested
    @DisplayName("makeCartProductReadResponse 테스트")
    class MakeCartProductReadResponseTest {
        @Test
        @DisplayName("성공적으로 리스폰스를 만듦.")
        void makeCartProductReadResponseTest01() {
            // given
            var account = makeAccount();

            var cart = makeCart(account);
            MockedStatic<Cart> mockCart = mockStatic(Cart.class);

            var cartProduct1 = makeCartProduct(cart, makeProduct(2L, "R2"), 1);
            var cartProduct2 = makeCartProduct(cart, makeProduct(3L, "R3"), 2);
            var cartProductList = List.of(cartProduct1, cartProduct2);

            var response1 =
                    makeCartProductReadResponse(2L, "R2", 1, "R2 이미지 네임", true);
            var response2 =
                    makeCartProductReadResponse(3L, "R3", 1, "R3 이미지 네임", true);

            var expected = List.of(response1, response2);

            given(Cart.createCart(any())).willReturn(cart);
            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(cartProductService.readByCartId(anyLong())).willReturn(cartProductList);

            given(cartProductService.readAllByCartIdAndProductIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
                    .willReturn(expected);

            // when
            final List<CartProductResponse.Read> actual = sut.makeCartProductReadResponse(account);

            // then
            assertIterableEquals(expected, actual);

            mockCart.close();
        }

        @Test
        @DisplayName("productList size가 0")
        void makeCartProductReadResponseTest02() {
            // given
            var account = makeAccount();

            var cart = makeCart(account);
            MockedStatic<Cart> mockCart = mockStatic(Cart.class);

            given(Cart.createCart(any())).willReturn(cart);
            given(cartService.readByAccountId(anyLong())).willReturn(cart);
            given(cartProductService.readByCartId(anyLong())).willReturn(new ArrayList<>());

            var expected = new ArrayList<>();

            // when
            final List<CartProductResponse.Read> actual = sut.makeCartProductReadResponse(account);

            // then
            assertIterableEquals(expected, actual);

            mockCart.close();
        }
    }

    @Nested
    @DisplayName("makeCartProductBuyResponse 테스트")
    class MakeCartProductBuyResponseTest {
        @Test
        @DisplayName("성공적으로 리스폰스를 만듦.")
        void makeCartProductBuyResponseTest01() {
            // given
            var account = makeAccount();
            var productIdList = List.of(1L, 2L);

            var cart = makeCart(account);
            MockedStatic<Cart> mockCart = mockStatic(Cart.class);
            given(Cart.createCart(any())).willReturn(cart);
            given(cartService.readByAccountId(anyLong())).willReturn(cart);

            var cartProductReadResponse1 =
                    makeCartProductReadResponse(1L, "R1", 1, "R1 이미지", true);
            var cartProductReadResponse2 =
                    makeCartProductReadResponse(2L, "R2", 1, "R2 이미지", false);
            var readList = List.of(cartProductReadResponse1, cartProductReadResponse2);

            given(Cart.createCart(any())).willReturn(cart);
            given(cartProductService.readAllByCartIdAndProductIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
                    .willReturn(readList);

            var productBuyResponse1 =
                    makeProductBuyResponse(1L, "R1", 3000000, "R1 설명");
            var productBuyResponse2 =
                    makeProductBuyResponse(2L, "R2", 4000000, "R2 설명");

            var productImageBuyResponse1 =
                    makeProductImageBuyResponse("R1", "R1 이미지", "/R1", true);
            var productImageBuyResponse2 =
                    makeProductImageBuyResponse("R2", "R2 이미지", "/R2", false);

            var cartProductBuyResponse1 =
                    makeCartProductBuyResponse(1, productBuyResponse1, productImageBuyResponse1);
            var cartProductBuyResponse2 =
                    makeCartProductBuyResponse(2, productBuyResponse2, productImageBuyResponse2);

            var expected = List.of(cartProductBuyResponse1, cartProductBuyResponse2);
            MockedStatic<CartProductResponse.Buy> mockCartProductBuyResponse = mockStatic(CartProductResponse.Buy.class);
            given(CartProductResponse.Buy.from(cartProductReadResponse1)).willReturn(cartProductBuyResponse1);
            given(CartProductResponse.Buy.from(cartProductReadResponse2)).willReturn(cartProductBuyResponse2);

            // when
            final List<CartProductResponse.Buy> actual = sut.makeCartProductBuyResponse(account, productIdList);

            // then
            assertEquals(expected, actual);

            mockCart.close();
            mockCartProductBuyResponse.close();
        }

        @Test
        @DisplayName("readList size가 0")
        void makeCartProductBuyResponseTest02() {
            // given
            var expected = new ArrayList<>();

            var account = makeAccount();
            var productIdList = List.of(1L, 2L);

            var cart = makeCart(account);
            MockedStatic<Cart> mockCart = mockStatic(Cart.class);
            given(Cart.createCart(any())).willReturn(cart);

            given(cartService.readByAccountId(anyLong())).willReturn(cart);

            given(Cart.createCart(any())).willReturn(cart);
            given(cartProductService.readAllByCartIdAndProductIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
                    .willReturn(new ArrayList<>());

            // when
            final List<CartProductResponse.Buy> actual = sut.makeCartProductBuyResponse(account, productIdList);

            // then
            assertEquals(expected, actual);

            mockCart.close();
        }
    }

    private ProductImageResponse.Buy makeProductImageBuyResponse(String imageName
            , String originImageName
            , String imageUrl
            , Boolean thumbnail) {

        var request = new ProductImageResponse.Buy();

        return request
                .setImageName(imageName)
                .setOriginImageName(originImageName)
                .setImageUrl(imageUrl)
                .setThumbnail(thumbnail);
    }

    private ProductResponse.Buy makeProductBuyResponse(Long productId
            , String productName
            , Integer price
            , String description) {
        var request = new ProductResponse.Buy();

        return request
                .setProductId(productId)
                .setProductName(productName)
                .setPrice(price)
                .setDescription(description);
    }

    private CartProductResponse.Buy makeCartProductBuyResponse(Integer count
            , ProductResponse.Buy product
            , ProductImageResponse.Buy productImage) {

        var request = new CartProductResponse.Buy();

        return request
                .setCount(count)
                .setProduct(product)
                .setProductImage(productImage);
    }


    private CartProductResponse.Read makeCartProductReadResponse(Long productId
            , String productName
            , Integer count
            , String imageName
            , Boolean isThumbnail) {

        var request = new CartProductResponse.Read();

        return request
                .setProductId(productId)
                .setCount(count)
                .setProductName(productName)
                .setPrice(1000)
                .setDescription("description")
                .setImageName(imageName)
                .setOriginImageName("originImageName")
                .setImageUrl("imageUrl")
                .setThumbnail(isThumbnail);
    }

    private Product makeProduct(Long id, String productName) {
        return Product.builder()
                .id(id)
                .productName(productName)
                .price(1000)
                .quantity(100)
                .soldCount(2)
                .description("상품1 설명")
                .productStatus(ProductStatus.SOLD_OUT)
                .category(null)
                .build();
    }

    private CartProduct makeCartProduct(Cart cart, Product product, int count) {
        return CartProduct.builder()
                .cart(cart)
                .product(product)
                .count(count)
                .build();
    }

    private Cart makeCart(Account account) {
        return Cart.builder()
                .id(1L)
                .account(account)
                .build();
    }

    private Account makeAccount() {
        return Account.builder()
                .id(1L)
                .build();
    }
}