package com.ecommerce.backend.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관관계 주인 -> fillAccount 만들어야 됨.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 연관관계 주인 아님
    @Setter
    @Builder.Default
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private List<Product> productList = new ArrayList<>();

    public void fillAccount(Account account) {
        this.account = account;
        account.setCart(this);
    }

    public static Cart createCart(Account account) {
        return Cart.builder()
                .account(account)
                .build();
    }
}
