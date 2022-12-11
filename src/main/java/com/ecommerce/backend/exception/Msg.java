package com.ecommerce.backend.exception;

public class Msg {
    // account
    public static final String ACCOUNT_NOT_FOUND = "ID, PASSWORD 올바르지 않습니다.";

    // address
    public static final String ADDRESS_NOT_FOUND = "주소를 찾을 수 없습니다.";

    // category
    public static final String CATEGORY_NOT_FOUND = "존재하지 않는 카테고리입니다.";

    // item
    public static final String PRODUCT_NOT_FOUND = "존재하지 않는 상품입니다.";

    // cart
    public static final String CART_NOT_FOUND = "장바구니를 찾을 수 없습니다.";

    // cartItem
    public static final String CART_PRODUCT_NOT_FOUND = "카트 혹은 프로덕트를 찾을 수 없습니다.";

    // prodcut
    public static final String PRODUCT_NOT_FOUND_IN_CART = "장바구니에서 해당 제품을 찾을 수 없습니다.";

    // itemImage
    public static final String PRODUCT_THUMBNAIL_NOT_FOUND = "썸네일 이미지를 찾을 수 없습니다.";
    public static final String PRODUCT_THUMBNAIL_REQUIRED = "상품 썸네일 이미지는 필수입니다.";
    public static final String PRODUCT_IMAGE_NOT_FOUND = "상품 이미지를 찾을 수 없습니다.";

    public static final String ORDER_DELIVERY_ADDRESS_NOT_FOUND = "배송지는 필수입니다.";
    public static final String ORDER_DELIVERY_RECEIVER_REQUIRED = "받는 사람은 필수입니다.";
    public static final String ORDER_NOT_FOUND = "존재하지 않는 주문 정보입니다.";
    public static final String ORDER_ALREADY_DELIVERY = "이미 배송된 상품은 취소가 불가능합니다";

    public static final String ORDER_PRODUCT_NOT_FOUND = "존재하지 않는 주문 제품입니다.";
}
