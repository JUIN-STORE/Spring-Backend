//package com.ecommerce.backend.domain.entity;
//
//import com.fasterxml.jackson.annotation.JsonIdentityInfo;
//import com.fasterxml.jackson.annotation.ObjectIdGenerators;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.experimental.Accessors;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.DynamicInsert;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//import java.util.List;
//@DynamicInsert
//@Entity @Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Accessors(chain = true)
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
//public class Cart {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long cartId;
//
//    // FIXME: 이거 왜 n to 1
//    //  - 이거 ㅋㅋㅋ 왜 n:1인가요
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_id")
//    private Account account;
//
//    @OneToMany(fetch = FetchType.LAZY,mappedBy="cart",cascade = CascadeType.ALL, orphanRemoval = true)    // 02-15 Megan
//    private List<CartItem> cartItems;
//
//    private String sessionId;
//
//    private String token;
//
//    @Column(columnDefinition = "enum('READY', 'ORDER', 'CANCEL')")
//    @Enumerated(EnumType.STRING)
//    private OrderStatus status;
//
//    private Integer itemPriceTotal;
//
//    private Integer itemDiscount;
//    /**
//     * 2021-03-01 결제 api 를 위해 남겨두었습니다
//     */
//    private Integer tax;
//
//    private Integer shipping;
//
//    private Integer userDiscount;
//
//    private Integer grandTotal;
//
//    // 2021-03-01 account 를 통해 획득할 수 있는 중복 필드를 제거했습니다
//
//    private String roadAddress;;
//
//    private String address;
//
//    private String city;
//
//    private String province;
//
//    private String country;
//
//    private Integer zipCode;
//
//    @Column(columnDefinition = "TEXT")
//    private String content;
//
//    @CreationTimestamp
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    private LocalDateTime updatedAt;
//
//    public static Cart createCart(Account account){
//        Cart cart = new Cart();
//        cart.sessionId = "";
//        cart.token = "";
//        cart.account = account;
//        Address address = account.getDefaultAddress();
//        cart.address = address.getAddress();
//        cart.roadAddress = address.getRoadAddress();
//        cart.city = address.getCity();
//        cart.province = address.getProvince();
//        cart.country = address.getCountry();
//        cart.zipCode = address.getZipCode();
//        return cart;
//    }
//
//    public void updateItem(CartItem cartItem) {
//        if (cartItem.getActive() == 1) {
//            this.itemPriceTotal = this.itemPriceTotal + cartItem.getPrice() * cartItem.getQuantity();
//            this.itemDiscount = this.itemDiscount + cartItem.getDiscountPrice() * cartItem.getQuantity();
//        } else if (cartItem.getActive() == 0) {
//            this.itemPriceTotal = this.itemPriceTotal - cartItem.getPrice() * cartItem.getQuantity();
//            this.itemDiscount = this.itemDiscount - cartItem.getDiscountPrice() * cartItem.getQuantity();
//        }
//
//    }
//
//    public void updateItem(Integer price, Integer quantity, Integer discountprice) {
//        this.itemPriceTotal = this.itemPriceTotal + price * quantity;
//        this.itemDiscount = this.itemDiscount + discountprice * quantity;
//    }
//}
