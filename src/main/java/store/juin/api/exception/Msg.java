package store.juin.api.exception;

public class Msg {
    // JWT
    public static final String INVALID_REFRESH_TOKEN = "Refresh Token 정보가 올바르지 않습니다.";

    // account
    public static final String WRONG_ID_PASSWORD = "ID, PASSWORD 올바르지 않습니다.";

    public static final String WRONG_PASSWORD = "PASSWORD가 올바르지 않습니다.";
    public static final String ACCOUNT_NOT_FOUND = "존재하지 않는 계정입니다.";
    public static final String DUPLICATED_ACCOUNT = "이미 존재하는 계정입니다.";
    public static final String DUPLICATED_IDENTIFICATION = "이미 사용 중인 아이디입니다.";

    // address
    public static final String ADDRESS_NOT_FOUND = "주소를 찾을 수 없습니다.";

    // category
    public static final String CATEGORY_NOT_FOUND = "존재하지 않는 카테고리입니다.";

    // item
    public static final String ITEM_NOT_FOUND = "존재하지 않는 상품입니다.";

    // cart
    public static final String CART_NOT_FOUND = "장바구니를 찾을 수 없습니다.";

    // cartItem
    public static final String CART_ITEM_NOT_FOUND = "카트 혹은 프로덕트를 찾을 수 없습니다.";

    // item
    public static final String ITEM_NOT_FOUND_IN_CART = "장바구니에서 해당 제품을 찾을 수 없습니다.";

    // itemImage
    public static final String ITEM_THUMBNAIL_NOT_FOUND = "썸네일 이미지를 찾을 수 없습니다.";
    public static final String ITEM_THUMBNAIL_REQUIRED = "상품 썸네일 이미지는 필수입니다.";
    public static final String ITEM_IMAGE_NOT_FOUND = "상품 이미지를 찾을 수 없습니다.";

    public static final String ILLEGAL_ITEM_IMAGE_FILE_NAME = "파일 이름에 문제가 있습니다.";

    public static final String ORDER_DELIVERY_ADDRESS_NOT_FOUND = "배송지는 필수입니다.";
    public static final String ORDER_DELIVERY_RECEIVER_REQUIRED = "받는 사람은 필수입니다.";
    public static final String ORDER_NOT_FOUND = "존재하지 않는 주문 정보입니다.";
    public static final String ORDER_ALREADY_DELIVERY = "이미 배송된 상품은 취소가 불가능합니다";

    public static final String ORDER_ITEM_NOT_FOUND = "존재하지 않는 주문 제품입니다.";

    public static final String DELIVERY_NOT_FOUND = "존재하지 않는 배송 정보입니다.";

    public static final String DELIVERY_INVALID_REQUEST = "올바르지 않는 배송 정보 조회 요청입니다.";
}
