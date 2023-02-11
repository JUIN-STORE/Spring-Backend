package store.juin.api.domain;

import store.juin.api.domain.response.CartItemResponse;
import store.juin.api.domain.response.ItemImageResponse;
import store.juin.api.domain.response.ItemResponse;

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
                .setName("hash-item1.jpg")
                .setOriginName("item1.jpg")
                .setImageUrl("http://localhost:8080/item/1")
                .setThumbnail(true);
    }
}