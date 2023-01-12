//package com.ecommerce.backend.relation;
//
//import com.ecommerce.backend.domain.entity.Account;
//import com.ecommerce.backend.domain.entity.Cart;
//import com.ecommerce.backend.domain.entity.CartItem;
//import com.ecommerce.backend.domain.entity.Item;
//import com.ecommerce.backend.domain.enums.ItemStatus;
//import com.ecommerce.backend.domain.response.CartItemResponse;
//import com.ecommerce.backend.domain.response.ItemImageResponse;
//import com.ecommerce.backend.domain.response.ItemResponse;
//import com.ecommerce.backend.service.query.CartItemQueryService;
//import com.ecommerce.backend.service.query.CartQueryService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertIterableEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.mockStatic;
//
//@ExtendWith(SpringExtension.class)
//class CartRelationServiceTest {
//    @InjectMocks
//    CartRelationService sut;
//
//    @Mock
//    CartQueryService cartQueryService;
//    @Mock
//    CartItemQueryService cartItemQueryService;
//
//    @Nested
//    @DisplayName("makeCartItemReadResponse 테스트")
//    class MakeCartItemReadResponseTest {
//        @Test
//        @DisplayName("성공적으로 리스폰스를 만듦.")
//        void makeCartItemReadResponseTest01() {
//            // given
//            var account = makeAccount();
//
//            var cart = makeCart(account);
//            MockedStatic<Cart> mockCart = mockStatic(Cart.class);
//
//            var cartItem1 = makeCartItem(cart, makeItem(2L, "R2"), 1);
//            var cartItem2 = makeCartItem(cart, makeItem(3L, "R3"), 2);
//            var cartItemList = List.of(cartItem1, cartItem2);
//
//            var response1 =
//                    makeCartItemReadResponse(2L, "R2", 1, "R2 이미지 네임", true);
//            var response2 =
//                    makeCartItemReadResponse(3L, "R3", 1, "R3 이미지 네임", true);
//
//            var expected = List.of(response1, response2);
//
//            given(Cart.createCart(any())).willReturn(cart);
//            given(cartQueryService.readByAccountId(anyLong())).willReturn(cart);
//            given(cartItemQueryService.readByCartId(anyLong())).willReturn(cartItemList);
//
//            given(cartItemQueryService.readAllByCartIdAndItemIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
//                    .willReturn(expected);
//
//            // when
//            final List<CartItemResponse.Read> actual = sut.makeCartItemReadResponse(account);
//
//            // then
//            assertIterableEquals(expected, actual);
//
//            mockCart.close();
//        }
//
//        @Test
//        @DisplayName("itemList size가 0")
//        void makeCartItemReadResponseTest02() {
//            // given
//            var account = makeAccount();
//
//            var cart = makeCart(account);
//            MockedStatic<Cart> mockCart = mockStatic(Cart.class);
//
//            given(Cart.createCart(any())).willReturn(cart);
//            given(cartQueryService.readByAccountId(anyLong())).willReturn(cart);
//            given(cartItemQueryService.readByCartId(anyLong())).willReturn(new ArrayList<>());
//
//            var expected = new ArrayList<>();
//
//            // when
//            final List<CartItemResponse.Read> actual = sut.makeCartItemReadResponse(account);
//
//            // then
//            assertIterableEquals(expected, actual);
//
//            mockCart.close();
//        }
//    }
//
//    @Nested
//    @DisplayName("makeCartItemBuyResponse 테스트")
//    class MakeCartItemBuyResponseTest {
//        @Test
//        @DisplayName("성공적으로 리스폰스를 만듦.")
//        void makeCartItemBuyResponseTest01() {
//            // given
//            var account = makeAccount();
//            var itemIdList = List.of(1L, 2L);
//
//            var cart = makeCart(account);
//            MockedStatic<Cart> mockCart = mockStatic(Cart.class);
//            given(Cart.createCart(any())).willReturn(cart);
//            given(cartQueryService.readByAccountId(anyLong())).willReturn(cart);
//
//            var cartItemReadResponse1 =
//                    makeCartItemReadResponse(1L, "R1", 1, "R1 이미지", true);
//            var cartItemReadResponse2 =
//                    makeCartItemReadResponse(2L, "R2", 1, "R2 이미지", false);
//            var readList = List.of(cartItemReadResponse1, cartItemReadResponse2);
//
//            given(Cart.createCart(any())).willReturn(cart);
//            given(cartItemQueryService.readAllByCartIdAndItemIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
//                    .willReturn(readList);
//
//            var itemBuyResponse1 =
//                    makeItemBuyResponse(1L, "R1", 3000000, "R1 설명");
//            var itemBuyResponse2 =
//                    makeItemBuyResponse(2L, "R2", 4000000, "R2 설명");
//
//            var itemImageBuyResponse1 =
//                    makeItemImageBuyResponse("R1", "R1 이미지", "/R1", true);
//            var itemImageBuyResponse2 =
//                    makeItemImageBuyResponse("R2", "R2 이미지", "/R2", false);
//
//            var cartItemBuyResponse1 =
//                    makeCartItemBuyResponse(1, itemBuyResponse1, itemImageBuyResponse1);
//            var cartItemBuyResponse2 =
//                    makeCartItemBuyResponse(2, itemBuyResponse2, itemImageBuyResponse2);
//
//            var expected = List.of(cartItemBuyResponse1, cartItemBuyResponse2);
//            MockedStatic<CartItemResponse.Buy> mockCartItemBuyResponse = mockStatic(CartItemResponse.Buy.class);
//            given(CartItemResponse.Buy.from(cartItemReadResponse1)).willReturn(cartItemBuyResponse1);
//            given(CartItemResponse.Buy.from(cartItemReadResponse2)).willReturn(cartItemBuyResponse2);
//
//            // when
//            final List<CartItemResponse.Buy> actual = sut.makeCartItemBuyResponse(account, itemIdList);
//
//            // then
//            assertEquals(expected, actual);
//
//            mockCart.close();
//            mockCartItemBuyResponse.close();
//        }
//
//        @Test
//        @DisplayName("readList size가 0")
//        void makeCartItemBuyResponseTest02() {
//            // given
//            var expected = new ArrayList<>();
//
//            var account = makeAccount();
//            var itemIdList = List.of(1L, 2L);
//
//            var cart = makeCart(account);
//            MockedStatic<Cart> mockCart = mockStatic(Cart.class);
//            given(Cart.createCart(any())).willReturn(cart);
//
//            given(cartQueryService.readByAccountId(anyLong())).willReturn(cart);
//
//            given(Cart.createCart(any())).willReturn(cart);
//            given(cartItemQueryService.readAllByCartIdAndItemIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
//                    .willReturn(new ArrayList<>());
//
//            // when
//            final List<CartItemResponse.Buy> actual = sut.makeCartItemBuyResponse(account, itemIdList);
//
//            // then
//            assertEquals(expected, actual);
//
//            mockCart.close();
//        }
//    }
//
//    private ItemImageResponse.Buy makeItemImageBuyResponse(String name
//            , String originName
//            , String imageUrl
//            , Boolean thumbnail) {
//
//        var request = new ItemImageResponse.Buy();
//
//        return request
//                .setName(name)
//                .setOriginName(originName)
//                .setImageUrl(imageUrl)
//                .setThumbnail(thumbnail);
//    }
//
//    private ItemResponse.Buy makeItemBuyResponse(Long itemId
//            , String name
//            , Integer price
//            , String description) {
//        var request = new ItemResponse.Buy();
//
//        return request
//                .setItemId(itemId)
//                .setItemName(name)
//                .setPrice(price)
//                .setDescription(description);
//    }
//
//    private CartItemResponse.Buy makeCartItemBuyResponse(Integer count
//            , ItemResponse.Buy item
//            , ItemImageResponse.Buy itemImage) {
//
//        var request = new CartItemResponse.Buy();
//
//        return request
//                .setCount(count)
//                .setItem(item)
//                .setItemImage(itemImage);
//    }
//
//
//    private CartItemResponse.Read makeCartItemReadResponse(Long itemId
//            , String itemName
//            , Integer count
//            , String itemImageName
//            , Boolean isThumbnail) {
//
//        var request = new CartItemResponse.Read();
//
//        return request
//                .setItemId(itemId)
//                .setCount(count)
//                .setItemName(itemName)
//                .setPrice(1000)
//                .setDescription("description")
//                .setItemImageName(itemImageName)
//                .setOriginImageName("originName")
//                .setImageUrl("imageUrl")
//                .setThumbnail(isThumbnail);
//    }
//
//    private Item makeItem(Long id, String name) {
//        return Item.builder()
//                .id(id)
//                .name(name)
//                .price(1000)
//                .quantity(100)
//                .soldCount(2)
//                .description("상품1 설명")
//                .itemStatus(ItemStatus.SOLD_OUT)
//                .category(null)
//                .build();
//    }
//
//    private CartItem makeCartItem(Cart cart, Item item, int count) {
//        return CartItem.builder()
//                .cart(cart)
//                .item(item)
//                .count(count)
//                .build();
//    }
//
//    private Cart makeCart(Account account) {
//        return Cart.builder()
//                .id(1L)
//                .account(account)
//                .build();
//    }
//
//    private Account makeAccount() {
//        return Account.builder()
//                .id(1L)
//                .build();
//    }
//}