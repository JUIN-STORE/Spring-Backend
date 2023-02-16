package store.juin.api.domain;

import store.juin.api.domain.enums.OrderStatus;
import store.juin.api.domain.response.*;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseUtil {
    public static CartItemResponse.Retrieve makeCartItemRetrieveResponse(String itemName) {
        return new CartItemResponse.Retrieve()
                .setItemId(1L)
                .setCount(1)
                .setItemName(itemName)
                .setPrice(1000)
                .setDescription("item1 description")
                .setItemImageName("item1.jpg")
                .setOriginImageName("item1.jpg")
                .setImageUrl("http://localhost:8080/item/1")
                .setThumbnail(true);
    }

    public static List<CartItemResponse.Retrieve> makeCartItemRetrieveResponseList() {
        return List.of(
                makeCartItemRetrieveResponse("item1"),
                makeCartItemRetrieveResponse("item2"),
                makeCartItemRetrieveResponse("item3")
        );
    }

    public static CartItemResponse.Buy makeCartItemBuyResponse() {
        return new CartItemResponse.Buy()
                .setCount(1)
                .setItem(makeItemBuyResponse())
                .setItemImage(makeItemImageBuyResponse());
    }

    public static List<CartItemResponse.Buy> makeCartItemBuyResponseList() {
        return List.of(
                makeCartItemBuyResponse(),
                makeCartItemBuyResponse(),
                makeCartItemBuyResponse()
        );
    }

    public static ItemResponse.Buy makeItemBuyResponse() {
        return new ItemResponse.Buy()
                .setItemId(1L)
                .setItemName("item1")
                .setPrice(1000)
                .setDescription("item1 description");
    }
    public static ItemImageResponse.Buy makeItemImageBuyResponse() {
        return new ItemImageResponse.Buy()
                .setName("상품명")
                .setImageName("item1.jpg")
                .setImageUrl("http://localhost:8080/item/1")
                .setThumbnail(true);
    }


    // categories
    public static List<CategoryResponse.Retrieve> makeCategoryRetrieveResponseList() {
        return List.of(
                makeCategoryRetrieveResponse(1L, "category1"),
                makeCategoryRetrieveResponse(2L, "category2")
        );
    }

    public static CategoryResponse.Retrieve makeCategoryRetrieveResponse(Long id, String categoryName) {
        return new CategoryResponse.Retrieve()
                .setId(id)
                .setCategoryName(categoryName)
                .setDepth(1L)
                .setChildList(List.of(makeCategoryRetrieveChildListResponse(id+1L, categoryName)));
    }

    public static CategoryResponse.RetrieveChildList makeCategoryRetrieveChildListResponse(Long id, String categoryName) {
        return new CategoryResponse.RetrieveChildList()
                .setId(id)
                .setCategoryName(categoryName + "_child")
                .setDepth(2L);
    }

    public static OrderJoinResponse makeOrderJoinResponse() {
        return new OrderJoinResponse()
                .setOrdersId(1L)
                .setOrderItemId(2L)
                .setItemId(15L)
                .setDeliveryId(222L)
                .setOrderCount(300)
                .setPrice(1000_000)
                .setName("리얼포스 R3")
                .setOrderDate(LocalDateTime.now())
                .setOrderStatus(OrderStatus.ORDER);
    }
}
