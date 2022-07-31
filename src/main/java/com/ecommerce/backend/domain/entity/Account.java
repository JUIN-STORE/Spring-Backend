package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.AccountRole;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity{
    @Id @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String passwordHash;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    @NotNull
    private LocalDateTime lastLogin;

    // 읽기 전용, 연관관계 주인 아님
    @Setter
    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private Cart cart;

    // 읽기 전용 -> addAddressList 만들어야 됨.
    @Builder.Default
    @Setter(value = AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Address> addressList = new ArrayList<>();

    // 읽기 전용 -> addOrderList 만들어야 됨.
    @Builder.Default
    @Setter(value = AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Order> orderList = new ArrayList<>();

    // 읽기용 매핑, 연관관계 주인 아님
    public void addAddressList(Address address){
        this.addressList.add(address);
        if (address.getAccount() != this){
            address.fillAccountRelation(this);
        }
    }

    // 읽기용 매핑, 연관관계 주인 아님
    public void addOrderList(Order order){
        this.orderList.add(order);
        if (order.getAccount() != this){
            order.fillAccountRelation(this);
        }
    }
}